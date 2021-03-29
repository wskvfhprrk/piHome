package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@RestController
@MapperScan("com.example.demo")
public class DemoApplication {


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private IoDao dao;

    @GetMapping("sqliteList")
    public List list() {
        IoEntity e = new IoEntity();
        e.setOpenInHex("555");
        e.setCloseInHex("555");
        e.setGroupName("555");
        e.setSwitchName("555");
        e.setStatus(true);
        e.setOpenOutHex("555");
        e.setCloseOutHex("555");
        e.setOpenOutHex("555");
        dao.save(e);
//        System.out.println("save====="+save);
        List<IoEntity> mapList = dao.selectAll();
        return mapList;
    }

}
