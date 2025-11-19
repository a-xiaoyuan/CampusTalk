package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 账户服务接口
 * 提供用户账户相关的业务逻辑操作
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Service
public interface AccountService extends IService<Account>, UserDetailsService {
    
    /**
     * 根据用户名查找账户
     * 
     * @param text 用户名或邮箱
     * @return 账户信息，未找到返回null
     */
    Account findAccountByUsername(String text);
    
    /**
     * 注册邮箱验证码发送
     * 
     * @param type 验证码类型（register/reset）
     * @param email 邮箱地址
     * @param ip 请求IP地址
     * @return 操作结果消息
     */
    String registerEmailVerifyCode(String type,String email,String ip);
    
    /**
     * 邮箱注册账户
     * 
     * @param vo 注册请求参数
     * @return 注册结果消息
     */
    String registerEmailAccount(EmailRegisterVO  vo);
    
    /**
     * 确认重置密码
     * 
     * @param vo 确认重置请求参数
     * @return 确认结果消息
     */
    String resetConfirm(ConfirmResetVO vo);
    
    /**
     * 重置邮箱账户密码
     * 
     * @param vo 重置密码请求参数
     * @return 重置结果消息
     */
    String resetEmailAccountPassword(EmailResetVO vo);
    Account findAccountById(int id);
}
