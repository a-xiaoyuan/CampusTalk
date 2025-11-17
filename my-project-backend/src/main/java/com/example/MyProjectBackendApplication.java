package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 项目主启动类
 * 负责启动Spring Boot应用并配置相关功能
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@EnableConfigurationProperties  // 启用配置属性支持
@EnableRabbit                    // 启用RabbitMQ消息队列支持
@EnableAsync                     // 启用异步处理支持
@SpringBootApplication(scanBasePackages = {"com.example"})  // Spring Boot应用主注解，扫描com.example包
@MapperScan("com.example.mapper")  // 扫描MyBatis映射器接口
public class MyProjectBackendApplication {

    /**
     * 应用主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MyProjectBackendApplication.class);
        app.setAllowCircularReferences(true);  // 允许循环引用，解决依赖注入时的循环依赖问题
        app.run(args);  // 启动Spring Boot应用
    }

}
