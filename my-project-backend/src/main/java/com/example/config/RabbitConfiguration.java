package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置类
 * 负责配置RabbitMQ相关的消息转换器、模板、监听器工厂和队列
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Configuration
public class RabbitConfiguration {
    
    /**
     * 配置消息转换器
     * 使用Jackson2JsonMessageConverter将消息转换为JSON格式
     * 
     * @return JSON消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * 配置RabbitTemplate
     * 用于发送消息到RabbitMQ队列
     * 
     * @param connectionFactory RabbitMQ连接工厂
     * @return 配置好的RabbitTemplate实例
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());  // 设置消息转换器
        return rabbitTemplate;
    }
    
    /**
     * 配置RabbitMQ监听器容器工厂
     * 用于处理从队列接收的消息
     * 
     * @param connectionFactory RabbitMQ连接工厂
     * @return 配置好的监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());  // 设置消息转换器
        return factory;
    }

    /**
     * 配置邮件队列
     * 创建一个持久化的邮件队列，用于存储邮件发送任务
     * 
     * @return 邮件队列实例
     */
    @Bean("mail")
    public Queue mailQueue(){
        return QueueBuilder
                .durable("mail")  // 创建持久化队列，确保消息不会丢失
                .build();
    }
}
