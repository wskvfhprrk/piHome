package com.example.demo;

import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/3 9:29
 */
@Service
@Slf4j
public class IoService {

    public static final String KEY = "list";

    @Value("${io.inCom}")
    private String inCom;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SerialUtils serialUtils;

    void add(IoEntity entity) {
        //缓存中添加
        redisTemplate.opsForList().rightPush(KEY, entity);
    }

    Result addAll(String str) {
        str = str.replaceAll(" ", "");
        str = str.toUpperCase(Locale.ROOT);
        String[] strings = str.split("\n");
        List<IoEntity> list = new ArrayList<>();
        for (String string : strings) {
            String[] split = string.split(",");
            IoEntity entity = new IoEntity();
            entity.setOpenInHex(split[0]);
            entity.setCloseInHex(split[1]);
            entity.setGroupName(split[2]);
            entity.setSwitchName(split[3]);
            entity.setStatus(Boolean.valueOf(split[4]));
            entity.setOpenOutHex(split[5]);
            entity.setCloseOutHex(split[6]);
            list.add(entity);
        }
        int size = list.size();
        //把输入或输出信号放在一起看是否有重复
        List<IoEntity> sumList = new ArrayList<>();
        for (IoEntity l : list) {
            IoEntity i = new IoEntity();
            BeanUtils.copyProperties(l, i);
            sumList.add(i);
            IoEntity i1 = new IoEntity();
            i1.setOpenInHex(l.getCloseInHex());
            sumList.add(i1);
            IoEntity i2 = new IoEntity();
            i2.setOpenInHex(l.getOpenOutHex());
            sumList.add(i2);
            IoEntity i3 = new IoEntity();
            i3.setOpenInHex(l.getCloseOutHex());
            sumList.add(i3);
        }
        long count = sumList.stream().map(IoEntity::getOpenInHex).distinct().count();
        if (sumList.size() > count) {
            return Result.error(500, "openInHex和closeInHex中有重复数据!");
        }
        //添加前先删除
        redisTemplate.delete(KEY);
        redisTemplate.opsForList().rightPushAll(KEY, list);
        return Result.ok();
    }

    Result delete(String inHex) {
        //查询所有值为list
        List<Map> list = getAll();

        //移除list中的值
        List<Map> mapList = list.stream()
                .filter(map -> !map.get("closeInHex").toString().equals(inHex.toUpperCase(Locale.ROOT)) && !map.get("openInHex").toString().equals(inHex.toUpperCase(Locale.ROOT)))
                .collect(Collectors.toList());
        //删除
        redisTemplate.delete(KEY);
        //把list批量添加
        redisTemplate.opsForList().rightPushAll(KEY, mapList);
        return Result.ok();
    }

    Result<Map> selectByInHex(String inHex) {
        //查询所有值为list
        List<Map> list = getAll();
        for (Map map : list) {
            if (map.get("openInHex").equals(inHex.toUpperCase(Locale.ROOT)) || map.get("closeInHex").equals(inHex.toUpperCase(Locale.ROOT))) {
                return Result.ok(map);
            }
        }
        return Result.error(500, "没有查询到数据");
    }

    public List<Map> getAll() {
        Long size = redisTemplate.opsForList().size(KEY);
        //查询所有值为list
        List<Map> list = redisTemplate.opsForList().range(KEY, 0, size);
        return list;
    }

    public Result<List<Map>> get() {
        return Result.ok(getAll());
    }


    public Result reset() {
        redisTemplate.delete(KEY);
        return Result.ok();
    }

    /**
     * 系统启动时监听红外发送射频信号——学习时发送红外信号
     */
    public void commandLine(String inHex) {
        final Serial serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        config.device(inCom)
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);
        try {
            serial.open(config);
            log.info("[device] " + config.device());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //发送inHex信号，写入redis
        try {
            if (inHex != null) {
                serial.write(StringToHex.hexStringToByteArray("A1F1" + inHex));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("serial start...............................");
        serial.addListener(event -> {
            try {
                log.info("[HEX DATA]   " + event.getHexByteString());
                //如果监听到F1则为发送红外信号返回值
                if (event.getHexByteString().equals("F1")) {
                    log.info("已经发送红外信号............");
                }
                String inHex1 = event.getHexByteString().replaceAll(",", "");
                if (inHex1 != null) {
                    Result<Map> mapResult = selectByInHex(inHex1);
                    if (mapResult.getCode() == 200) {
                        log.info(mapResult.getContent().toString());
                        //发送数据
                        try {
                            String outHex = "";
                            //如果是打开开关信号
                            if (mapResult.getContent().get("openInHex").equals(inHex1)) {
                                outHex = mapResult.getContent().get("openOutHex").toString();
                                //把其状态更改为打开
                                updateStastus(mapResult.getContent(), true);
                            } else { //否则为关闭开关信息
                                outHex = mapResult.getContent().get("closeOutHex").toString();
                                //把其状态更改为关闭
                                updateStastus(mapResult.getContent(), false);
                            }
                            serialUtils.sendHex(outHex);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 修改数据状态
     *
     * @param map
     * @param b
     */
    public void updateStastus(Map map, boolean b) {
        log.info("status:" + b);
        //删除此条数据
        delete(map.get("openInHex").toString());
        //查出删除后的所有数据
        List<Map> all = getAll();
        map.put("status", b);
        log.info("更新后的map:" + map.toString());
        //把状态修改后的数据添加进去
        all.add(map);
        //重新更新redis数据
        redisTemplate.delete(KEY);
        redisTemplate.opsForList().rightPushAll(KEY, all);
    }
}