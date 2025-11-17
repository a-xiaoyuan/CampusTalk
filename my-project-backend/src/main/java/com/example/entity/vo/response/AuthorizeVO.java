package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 认证响应VO类
 * 用于返回用户认证成功后的信息
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Data  // Lombok注解，自动生成getter/setter等方法
public class AuthorizeVO {
    
    /**
     * 用户名
     */
    String username;
    
    /**
     * 用户角色
     */
    String role;
    
    /**
     * JWT令牌
     */
    String token;
    
    /**
     * 令牌过期时间
     */
    Date expire;
}
