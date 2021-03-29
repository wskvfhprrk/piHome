package com.example.demo;

import com.pi4j.io.gpio.*;
import org.springframework.stereotype.Service;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/24 21:40
 */
@Service
public class Gpio {
    /**
     * 蜂鸣器响应声音
     * @param num 响几声
     * @throws InterruptedException
     */
    // 创建gpio控制器——实用了单例模式的工厂类
    final GpioController gpio = GpioFactory.getInstance();
    // gpio引脚#01作为输出引脚并打开——RaspiPin.GPIO_01为第18针
    final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "sound", PinState.LOW);
    public void sendSound(int num)  {
       for (int i = 0; i < num; i++) {
            //声音100毫秒短催较好
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //high为高电平
            pin.high();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //低电平
            pin.low();
        }
        //pin关闭，设置状态为low
        pin.setShutdownOptions(true, PinState.LOW);
    }
}
