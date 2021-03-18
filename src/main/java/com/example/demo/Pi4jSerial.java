package com.example.demo;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;


/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/2 12:57
 * pi4j官网串口demo
 */
public class Pi4jSerial {
    /**
     * This example program supports the following optional command arguments/options:
     * "--device (device-path)"                   [DEFAULT: /dev/ttyAMA0]
     * "--baud (baud-rate)"                       [DEFAULT: 38400]
     * "--data-bits (5|6|7|8)"                    [DEFAULT: 8]
     * "--parity (none|odd|even)"                 [DEFAULT: none]
     * "--stop-bits (1|2)"                        [DEFAULT: 1]
     * "--flow-control (none|hardware|software)"  [DEFAULT: none]
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public static void test() throws InterruptedException, IOException {
//    public static void main(String args[]) throws InterruptedException, IOException {

        // !! ATTENTION !!
        // By default, the serial port is configured as a console port
        // for interacting with the Linux OS shell.  If you want to use
        // the serial port in a software program, you must disable the
        // OS from using this port.
        //
        // Please see this blog article for instructions on how to disable
        // the OS console for this port:
        // https://www.cube-controls.com/2015/11/02/disable-serial-port-terminal-output-on-raspbian/

        // 创建Pi4J控制台包装器/助手
        // (这是一个用于抽象一些样板代码的实用程序类)
        final Console console = new Console();

        // 打印程序标题/头
        console.title("<-- The Pi4J Project -->", "Serial Communication Example");

        // 允许用户退出程序使用CTRL-C
        console.promptForExit();

        // 创建serial communications类的实例
        final Serial serial = SerialFactory.createInstance();

        // 创建并注册串行数据侦听器
        serial.addListener(event -> {

            //注意!-从串口读取。如果它没有从接收缓冲区读取，则缓冲区将继续增长并消耗内存。

            // 打印出接收到控制台的数据
            try {
                console.println("[HEX DATA]   " + event.getHexByteString());
                console.println("[ASCII DATA] " + event.getAsciiString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            // 创建串行配置对象
            SerialConfig config = new SerialConfig();

            // 设置默认的串行设置(设备，波特率，流量控制等)
            //
            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
            // NOTE: this utility method will determine the default serial port for the
            //       detected platform and board/model.  For all Raspberry Pi models
            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
            //       environment configuration.
//            config.device(SerialPort.getDefaultPort())
            config.device("/dev/ttyAMA0")
                    .baud(Baud._38400)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            // 解析可选的命令参数选项，以覆盖默认的串行设置.

            // 显示连接细节
            console.box(" Connecting to: " + config.toString(),
                    " We are sending ASCII data on the serial port every 1 second.",
                    " Data received on serial port will be displayed below.");


            // 使用配置设置打开默认的串行设备/端口
            while (true){
                if(serial.isClosed()){
                    console.println("openSerial......");
                    serial.open(config);
                }
            }

            // 连续循环，以保持程序运行，直到用户终止程序
//            while (console.isRunning()) {
//                try {
//                    // 将格式化字符串写入串行传输缓冲区
//                    String str = "A1F14DEBF8";
//                    serial.write(StringToHex.hexStringToByteArray(str));
//                } catch (IllegalStateException ex) {
//                    ex.printStackTrace();
//                }
//
//                // 阻塞1秒继续
//                Thread.sleep(1000);
//            }

            // 关闭串口
//            serial.close();
        } catch (IOException ex) {
            console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }
}
