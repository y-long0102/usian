package com.usian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApp{

    public static void main(String[] args) {
        System.out.println("我刚刚增加了内容。。。");
        SpringApplication.run(EurekaApp.class, args);
    }
}