package com.example.demo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    IoService dao;

    @Test
    void contextLoads() {
    }


    @Test
    public void delete(){
        dao.delete("1");
    }

    @Test
    public void addAll(){
        String str="12,s,d,true,4;11,s,d,true,3;";
        Result s = dao.addAll(str);
        System.out.println(s.getCode());
    }

    @Test
    public void getAll(){
//        dao.getAll1();
    }

}
