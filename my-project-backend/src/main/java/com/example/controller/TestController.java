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

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Resource
    private AmqpTemplate amqpTemplate;
    
    @Resource
    private JavaMailSender sender;

    @GetMapping("/hello")
    public String test(){
        return "hello world";
    }

    @GetMapping("/send-mail")
    public RestBean<Void> testMail(@RequestParam String email) {
        try {
            // 直接发送邮件测试
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("【系统通知】邮箱验证码");
            message.setText("尊敬的用户：\n\n" +
                    "您好！\n\n" +
                    "您正在进行邮箱验证操作，验证码为：123456\n\n" +
                    "有效时间：3分钟\n\n" +
                    "温馨提示：\n" +
                    "- 请勿将验证码泄露给他人\n" +
                    "- 如非本人操作，请忽略此邮件\n\n" +
                    "此邮件由系统自动发送，请勿回复。\n\n" +
                    "系统管理员");
            message.setTo(email);
            message.setFrom("javajwtli111@163.com");
            
            sender.send(message);
            System.out.println("直接邮件发送成功到: " + email);
            
            // 同时也发送到RabbitMQ
            Map<String, Object> data = Map.of(
                    "type", "register",
                    "email", email,
                    "code", 123456
            );
            amqpTemplate.convertAndSend("mail", data);
            System.out.println("RabbitMQ消息已发送");
            
            return RestBean.success();
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
            return RestBean.failure(500, "邮件发送失败: " + e.getMessage());
        }
    }
}
