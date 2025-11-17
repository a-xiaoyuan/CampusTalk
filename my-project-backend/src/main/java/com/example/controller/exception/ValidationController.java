package com.example.controller.exception;

import com.example.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 参数验证异常处理控制器
 * 统一处理参数验证相关的异常，提供友好的错误响应
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Slf4j  // 日志注解
@RestControllerAdvice  // 全局异常处理注解
public class ValidationController {
    
    /**
     * 处理参数验证异常
     * 捕获ValidationException类型的异常，返回统一的错误响应
     * 
     * @param exception 验证异常对象
     * @return 错误响应结果
     */
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validationException(ValidationException exception){
        log.warn("Resolve [{}: {}]",exception.getClass().getName(),exception.getMessage());  // 记录警告日志
        return RestBean.failure(400,"请求参数错误");  // 返回400错误响应
    }
}
