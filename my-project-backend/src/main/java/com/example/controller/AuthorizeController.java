package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 认证控制器
 * 处理用户注册、登录、密码重置等认证相关请求
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Validated  // 启用参数验证
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    
    @Resource
    AccountService service;  // 用户服务，处理用户相关业务逻辑
    
    /**
     * 请求验证码接口
     * 根据邮箱和类型（注册/重置）发送验证码邮件
     * 
     * @param email 邮箱地址（必须符合邮箱格式）
     * @param type 验证码类型（register-注册，reset-重置密码）
     * @param request HTTP请求对象，用于获取客户端IP地址
     * @return 操作结果
     */
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = ("register|reset|modify")) String type,
                                        HttpServletRequest request){
        return this.messageHandle(()->
                service.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }
    
    /**
     * 邮箱注册接口
     * 使用邮箱和验证码完成用户注册
     * 
     * @param vo 注册请求参数对象
     * @return 注册结果
     */
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Validated EmailRegisterVO vo){
        return this.messageHandle(vo,service::registerEmailAccount);
    }
    
    /**
     * 重置密码确认接口
     * 验证重置密码的验证码是否正确
     * 
     * @param vo 重置确认请求参数对象
     * @return 确认结果
     */
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Validated ConfirmResetVO vo){
        return this.messageHandle(vo,service::resetConfirm);
    }
    
    /**
     * 重置密码接口
     * 使用验证码重置用户密码
     * 
     * @param vo 重置密码请求参数对象
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody @Validated EmailResetVO vo){
        return this.messageHandle(vo,service::resetEmailAccountPassword);
    }
    
    /**
     * 通用消息处理方法（带参数）
     * 统一处理业务逻辑执行结果
     * 
     * @param <T> 参数类型
     * @param vo 请求参数对象
     * @param function 业务逻辑函数
     * @return 处理结果
     */
    private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
        return messageHandle(()->function.apply(vo));
    }
    
    /**
     * 通用消息处理方法（无参数）
     * 统一处理业务逻辑执行结果
     * 
     * @param action 业务逻辑执行函数
     * @return 处理结果（成功返回success，失败返回错误信息）
     */
    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        return message==null? RestBean.success():RestBean.failure(400,message);
    }
}
