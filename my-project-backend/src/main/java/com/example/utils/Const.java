package com.example.utils;

/**
 * 常量定义类
 * 包含项目中使用的各种常量，如Redis键前缀、过滤器顺序等
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
public class Const {
    
    /**
     * JWT黑名单Redis键前缀
     */
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    
    /**
     * 邮箱验证码频率限制Redis键前缀
     */
    public static final String VERIFY_EMAIL_LIMIT="verify:email:limit:";
    
    /**
     * 邮箱验证码数据Redis键前缀
     */
    public static final String VERIFY_EMAIL_DATA="verify:email:data:";
    
    /**
     * CORS过滤器执行顺序
     */
    public static final int ORDER_CORS=-102;
    
    /**
     * 限流过滤器执行顺序
     */
    public static final int ORDER_LIMIT=-101;
    
    /**
     * 流量限制计数器Redis键前缀
     */
    public static final String FLOW_LIMIT_COUNTER="flow:counter:";
    
    /**
     * 流量限制阻塞Redis键前缀
     */
    public static final String FLOW_LIMIT_BLOCK="flow:block:";
}
