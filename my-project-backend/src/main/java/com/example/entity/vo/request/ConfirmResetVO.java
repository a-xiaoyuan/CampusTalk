package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 确认重置密码请求VO类
 * 用于接收用户确认重置密码的请求参数（验证邮箱和验证码）
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Data  // Lombok注解，自动生成getter/setter等方法
@AllArgsConstructor  // 生成全参构造器
public class ConfirmResetVO {
    
    /**
     * 邮箱地址
     * 必须符合邮箱格式
     */
    @Email  // 邮箱格式验证
    String email;
    
    /**
     * 验证码
     * 必须为6位数字或字母
     */
    @Length(min = 6, max = 6)  // 固定长度6位
    String code;
}
