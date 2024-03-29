package com.yupi.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yupi.project.mapper")
public class MyApplication {


    public static void main(String[] args) {
        String version = SpringBootVersion.getVersion();
        System.out.println(version+",,,,,,,,,,,,,,,,,,,,,,,,,,,,");
        SpringApplication.run(MyApplication.class, args);
    }

}
