package com.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MailQueueListener {

    @Resource
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String username;

    @RabbitListener(queues = "mail")
    public void sendMailMessage(Map<String, Object> data) {
        log.info("收到邮件发送请求: {}", data);
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");

        log.info("准备发送邮件到: {}, 验证码: {}, 类型: {}", email, code, type);

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
            default -> {
                log.warn("未知的邮件类型: {}", type);
                yield null;
            }
        };

        if (message == null) {
            log.warn("邮件消息为空，跳过发送");
            return;
        }

        try {
            sender.send(message);
            log.info("邮件发送成功到: {}", email);
        } catch (Exception e) {
            log.error("邮件发送失败到: {}, 错误: {}", email, e.getMessage(), e);
        }
    }

    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
