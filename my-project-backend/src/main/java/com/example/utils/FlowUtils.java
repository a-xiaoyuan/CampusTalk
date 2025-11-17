package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 流量控制工具类
 * 提供基于Redis的请求频率限制功能
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Component
public class FlowUtils {
    
    /**
     * Redis模板，用于操作Redis数据库
     */
    @Resource
    StringRedisTemplate template;
    
    /**
     * 一次性限制检查
     * 检查指定键是否已存在，不存在则设置并返回true，存在则返回false
     * 
     * @param key Redis键名
     * @param blockTime 阻塞时间（秒）
     * @return 是否允许通过（true：允许，false：拒绝）
     */
    public boolean limitOnceCheck(String key,int blockTime){
        if(Boolean.TRUE.equals(template.hasKey(key))){
            return false;  // 键已存在，拒绝请求
        }else{
            template.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);  // 设置键并指定过期时间
            return true;  // 键不存在，允许请求
        }
    }

}
