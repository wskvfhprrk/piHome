package com.example.demo;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * 树莓派工具类
 *
 * @author hejz
 * @version 1.0
 * @date 2021/3/2 17:35
 */
@Component
@Slf4j
public class SerialUtils {


    public static final String OUT_HEX = "outHex";
    private static String inCom;

    private static String outCom;
    @Autowired
    private Gpio gpioService;

    @Value("${io.inCom}")
    public void setInCom(String inCom) {
        this.inCom = inCom;
    }

    @Value(("${io.outCom}"))
    public void setOutCom(String outCom) {
        this.outCom = outCom;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送16进制数据——加锁实行单线程，一次处理一个发送命令
     *
     * @param hexString
     * @return
     */
    public synchronized String sendHex(String hexString) throws IOException{
        log.info("hexString:{}",hexString);
        //超过2分钟不再发送了
        redisTemplate.opsForValue().set(OUT_HEX, hexString.substring(6, 14), Duration.ofMillis(2000L));
        //发送命令后报警一声
        gpioService.sendSound(1);
        return send(hexString);
    }

    private String send(String hexString) throws IOException {
        final Serial serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        config.device(outCom)
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);
        serial.open(config);
        serial.sendBreak();
        //先移除监听，然后再监听一下
//        serial.removeListener();
        serial.addListener(event -> {
            try {
                log.info("发送射频返回的值：" + event.getHexByteString());
                //返回值与原值比对，如果命令是发送的就不重新发送
                //先查redis中是否有这个值
                Object o = redisTemplate.opsForValue().get(OUT_HEX);
                if (o != null) {
                    if (event.getHexByteString().replaceAll(",", "").equals(o.toString())) {
                        redisTemplate.delete(OUT_HEX);
                        log.info("发送成功");
                    } else {
                        //没有比对成功重新发送
                        log.info("发送不成功，重新发送。");
                        Thread.sleep(10L);
                        sendHex(hexString);
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        log.info("发送射的值：" + hexString);
//        send(serial,hexString);
        serial.write(StringToHex.hexStringToByteArray(hexString));
        return "ok";
    }

    private static void send(Serial serial, String hexString) {
        for (int i = 0; i < 100; i++) {
            try {
                log.info("发送第" + i + "次！");
                serial.write(StringToHex.hexStringToByteArray(hexString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void getHex() throws IOException {
        final Console console = new Console();
        final Serial serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        config.device(inCom)
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);
        console.println("inCom==========" + inCom);
        serial.open(config);
        console.title("<-- The Pi4J Project -->", "Serial start");
        new Thread(() -> {
            while (true) {
                byte[] read = new byte[0];
                try {
                    read = serial.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String string = StringToHex.bytesToHexString(read);
                console.println(string);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void main(String[] args) {
        String str = "FD010101010260DF";
        System.out.println(str.substring(4, 14));
    }
}
