package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

/**
 * 统一响应结果封装类
 * 使用Java Record特性，提供标准化的API响应格式
 * 
 * @param <T> 响应数据类型
 * @param code 状态码
 * @param data 响应数据
 * @param message 响应消息
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
public record RestBean<T>(int code, T data, String message) {

    /**
     * 成功响应（带数据）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "请求成功");
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> RestBean<T> success(){
        return success(null);
    }
    
    /**
     * 失败响应
     * 
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(code, null, message);
    }

    /**
     * 未认证响应（401错误）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 未认证响应对象
     */
    public static <T> RestBean<T> unauthorized(String message) {
        return failure(401, message);
    }
    
    /**
     * 禁止访问响应（403错误）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 禁止访问响应对象
     */
    public static <T> RestBean<T> forbidden(String message) {
        return failure(403, message);
    }

    /**
     * 将响应对象转换为JSON字符串
     * 
     * @return JSON格式的字符串
     */
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}