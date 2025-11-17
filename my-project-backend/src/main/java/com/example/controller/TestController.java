package com.example.controller;

import com.example.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 测试控制器
 * 提供测试接口，用于验证系统功能和邮件发送
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Resource
    private AmqpTemplate amqpTemplate;  // RabbitMQ消息模板，用于发送消息到队列
    
    @Resource
    private JavaMailSender sender;  // Java邮件发送器，用于直接发送邮件

    /**
     * 基础测试接口
     * 用于验证服务是否正常运行
     * 
     * @return "hello world"字符串
     */
    @GetMapping("/hello")
    public String test(){
        return "hello world";
    }

    /**
     * 邮件发送测试接口
     * 测试直接邮件发送和RabbitMQ消息队列功能
     * 
     * @param email 收件人邮箱地址
     * @return 发送结果
     */
    @GetMapping("/send-mail")
    public RestBean<Void> testMail(@RequestParam String email) {
        try {
            // 直接发送邮件测试
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("【系统通知】邮箱验证码");  // 邮件主题
            message.setText("尊敬的用户：\n\n" +
                    "您好！\n\n" +
                    "您正在进行邮箱验证操作，验证码为：123456\n\n" +
                    "有效时间：3分钟\n\n" +
                    "温馨提示：\n" +
                    "- 请勿将验证码泄露给他人\n" +
                    "- 如非本人操作，请忽略此邮件\n\n" +
                    "此邮件由系统自动发送，请勿回复。\n\n" +
                    "系统管理员");  // 邮件内容
            message.setTo(email);  // 收件人邮箱
            message.setFrom("javajwtli111@163.com");  // 发件人邮箱
            
            sender.send(message);  // 发送邮件
            System.out.println("直接邮件发送成功到: " + email);
            
            // 同时也发送到RabbitMQ，测试消息队列功能
            Map<String, Object> data = Map.of(
                    "type", "register",  // 邮件类型
                    "email", email,      // 收件人邮箱
                    "code", 123456       // 验证码
            );
            amqpTemplate.convertAndSend("mail", data);  // 发送消息到mail队列
            System.out.println("RabbitMQ消息已发送");
            
            return RestBean.success();  // 返回成功结果
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
            return RestBean.failure(500, "邮件发送失败: " + e.getMessage());  // 返回失败结果
        }
    }
}
