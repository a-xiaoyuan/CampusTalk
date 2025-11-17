package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 邮箱注册请求VO类
 * 用于接收用户通过邮箱注册的请求参数
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Data  // Lombok注解，自动生成getter/setter等方法
public class EmailRegisterVO {
    
    /**
     * 邮箱地址
     * 必须符合邮箱格式，长度至少4位
     */
    @Email  // 邮箱格式验证
    @Length(min = 4)  // 最小长度4位
    String email;
    
    /**
     * 验证码
     * 必须为6位数字或字母
     */
    @Length(min = 6,max = 6)  // 固定长度6位
    String code;
    
    /**
     * 用户名
     * 支持中文、英文、数字，长度1-10位
     */
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")  // 允许中文、英文、数字
    @Length(min = 1,max = 10)  // 长度1-10位
    String username;
    
    /**
     * 密码
     * 长度6-20位
     */
    @Length(min = 6,max = 20)  // 长度6-20位
    String password;
}
