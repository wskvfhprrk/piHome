package com.example.demo;

import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/3 9:29
 */
@Service
@Slf4j
public class IoService {


    @Value("${io.inCom}")
    private String inCom;
    @Autowired
    private IoDao dao;
    @Autowired
    private SerialUtils serialUtils;

    void add(IoEntity entity) {
        //缓存中添加
        dao.save(entity);
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
        dao.deleteAll();
        list.stream().forEach(l -> dao.save(l));
        return Result.ok();
    }

    Result delete(String inHex) {
        dao.delete(inHex);
        return Result.ok();
    }

    Result<IoEntity> selectByInHex(String inHex) {
        //查询所有值为list
        List<IoEntity> list = getAll();
        for (IoEntity map : list) {
            if (map.getOpenInHex().equals(inHex.toUpperCase(Locale.ROOT)) || map.getCloseInHex().equals(inHex.toUpperCase(Locale.ROOT))) {
                return Result.ok(map);
            }
        }
        return Result.error(500, "没有查询到数据");
    }

    public List<IoEntity> getAll() {
        return dao.selectAll();
    }

    public Result<List<Map>> get() {
        return Result.ok(getAll());
    }


    public Result reset() {
        dao.deleteAll();
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
                IoEntity ioEntity;
                if (inHex1 != null) {
                    ioEntity = dao.selectByOpenInHex(inHex1);
                    if (ioEntity != null) {
                    } else {
                        ioEntity = dao.selectByCloseInHex(inHex1);
                    }
                    //发送数据
                    try {
                        String outHex = "";
                        //如果是打开开关信号
                        if (ioEntity.getOpenInHex().equals(inHex1)) {
                            outHex = ioEntity.getOpenOutHex();
                            //把其状态更改为打开
                            ioEntity.setStatus(true);
                            dao.updateStatus(ioEntity);
                        } else { //否则为关闭开关信息
                            outHex = ioEntity.getCloseOutHex();
                            //把其状态更改为关闭
                            ioEntity.setStatus(false);
                            dao.updateStatus(ioEntity);
                        }
                        serialUtils.sendHex(outHex);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}