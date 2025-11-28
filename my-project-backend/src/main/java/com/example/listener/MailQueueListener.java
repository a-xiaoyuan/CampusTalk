package com.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 邮件队列监听器
 * 监听RabbitMQ的mail队列，处理邮件发送任务
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Slf4j  // 启用日志记录
@Component
@RabbitListener(queues = "mail")  // 监听mail队列
public class MailQueueListener {

    @Resource
    private JavaMailSender sender;  // 邮件发送器

    @Value("${spring.mail.username}")
    private String username;  // 发件人邮箱地址

    /**
     * 处理邮件发送消息
     * 从RabbitMQ队列接收邮件发送请求并执行发送操作
     * 
     * @param data 邮件数据，包含邮箱、验证码、类型等信息
     */
    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        log.info("收到邮件发送请求: {}", data);
        
        // 从消息中提取邮件相关信息
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");

        log.info("准备发送邮件到: {}, 验证码: {}, 类型: {}", email, code, type);

        // 根据邮件类型创建不同的邮件内容
        SimpleMailMessage message = switch (type) {
            case "register" -> createMessage("【系统通知】注册验证码",
                    "尊敬的用户：\n\n" +
                    "您好！\n\n" +
                    "欢迎注册我们的服务！\n\n" +
                    "您的邮箱验证码为：" + code + "\n\n" +
                    "有效时间：3分钟\n\n" +
                    "温馨提示：\n" +
                    "- 请勿将验证码泄露给他人\n" +
                    "- 如非本人操作，请忽略此邮件\n\n" +
                    "此邮件由系统自动发送，请勿回复。\n\n" +
                    "系统管理员", email);
            case "reset" -> createMessage("【系统通知】密码重置验证码",
                    "尊敬的用户：\n\n" +
                    "您好！\n\n" +
                    "您正在进行密码重置操作。\n\n" +
                    "您的邮箱验证码为：" + code + "\n\n" +
                    "有效时间：3分钟\n\n" +
                    "温馨提示：\n" +
                    "- 请勿将验证码泄露给他人\n" +
                    "- 如非本人操作，请立即联系客服\n\n" +
                    "此邮件由系统自动发送，请勿回复。\n\n" +
                    "系统管理员", email);
            case "modify" -> createMessage("【系统通知】修改邮件验证码",
                    "尊敬的用户：\n\n" +
                    "您好！\n\n" +
                    "您正在进行修改邮件操作。\n\n" +
                    "您的邮箱验证码为：" + code + "\n\n" +
                    "有效时间：3分钟\n\n" +
                    "温馨提示：\n" +
                    "- 请勿将验证码泄露给他人\n" +
                    "- 如非本人操作，请立即联系客服\n\n" +
                    "此邮件由系统自动发送，请勿回复。\n\n" +
                    "系统管理员", email
            );
            default -> {
                log.warn("未知的邮件类型: {}", type);
                yield null;  // 未知类型返回null
            }
        };

        // 检查邮件消息是否为空
        if (message == null) {
            log.warn("邮件消息为空，跳过发送");
            return;
        }

        try {
            sender.send(message);  // 发送邮件
            log.info("邮件发送成功到: {}", email);
        } catch (Exception e) {
            log.error("邮件发送失败到: {}, 错误: {}", email, e.getMessage(), e);
        }
    }

    /**
     * 处理字节数组格式的消息（兼容性处理）
     * @param bytes 字节数组消息体
     */
    @RabbitHandler
    public void handleByteArrayMessage(byte[] bytes) {
        log.warn("接收到未处理的字节数组消息，长度: {}", bytes != null ? bytes.length : 0);
    }

    /**
     * 创建邮件消息对象
     * 
     * @param title 邮件标题
     * @param content 邮件内容
     * @param email 收件人邮箱
     * @return 配置好的邮件消息对象
     */
    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);  // 设置邮件标题
        message.setText(content);   // 设置邮件内容
        message.setTo(email);       // 设置收件人
        message.setFrom(username);  // 设置发件人
        return message;
    }
}
