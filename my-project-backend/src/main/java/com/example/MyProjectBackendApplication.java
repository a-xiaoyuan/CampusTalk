package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.example"})
@MapperScan("com.example.mapper")
public class MyProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MyProjectBackendApplication.class);
        app.setAllowCircularReferences(true);
        app.run(args);
    }

}
