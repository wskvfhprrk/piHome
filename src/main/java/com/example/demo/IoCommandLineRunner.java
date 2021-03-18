package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/3 23:10
 */
@Component
@Slf4j
public class IoCommandLineRunner implements CommandLineRunner {

    @Autowired
    private IoService ioService;
    @Override
    public void run(String... args) throws Exception {
        //启动时启动
//        ioService.commandLine(null);
    }
}
