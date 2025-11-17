package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 实现Spring Security的UserDetailsService接口，用于用户认证
 * 提供用户注册、登录、密码重置等核心业务逻辑
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    StringRedisTemplate stringRedisTemplate;  // Redis模板，用于缓存操作
    
    @Resource
    AmqpTemplate amqpTemplate;  // RabbitMQ模板，用于消息队列操作
    
    @Resource
    FlowUtils utils;  // 流量控制工具类
    
    @Resource
    PasswordEncoder encoder;  // 密码编码器，用于密码加密
    /**
     * 根据用户名加载用户信息（Spring Security认证核心方法）
     * 这个方法会被Spring Security自动调用进行用户认证
     * 
     * @param username 用户名
     * @return UserDetails 用户详情对象
     * @throws UsernameNotFoundException 用户名不存在时抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        Account account = this.findAccountByUsername(username);
        
        // 如果用户不存在，抛出异常（Spring Security会将其转换为"Bad credentials"错误）
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        
        // 构建Spring Security的UserDetails对象
        return User
                .withUsername(username)  // 设置用户名
                .password(account.getPassword())  // 设置密码（数据库中存储的是BCrypt加密后的密码）
                .roles(account.getRole())  // 设置用户角色
                .build();
    }
    /**
     * 邮箱注册账户实现
     * 验证邮箱验证码、检查邮箱和用户名是否已存在，创建新用户账户
     * 
     * @param vo 注册请求参数
     * @return 注册结果消息，成功返回null，失败返回错误信息
     */
    @Override
    public String registerEmailAccount(EmailRegisterVO vo){
        String email=vo.getEmail();
        String username=vo.getUsername();
        String key=Const.VERIFY_EMAIL_DATA+email;
        
        // 从Redis获取验证码
        String code=stringRedisTemplate.opsForValue().get(key);
        if(code==null) return "请先获取验证码";
        
        // 验证验证码是否正确
        if(!code.equals(vo.getCode()))
            return "验证码错误";
            
        // 检查邮箱是否已存在
        if(this.existsAccountByEmail(email))
            return "邮箱已存在";
            
        // 检查用户名是否已存在
        if(this.existsAccountByUsername(username))
            return "用户名已存在";
            
        // 加密密码并创建账户
        String password=encoder.encode(vo.getPassword());
        Account account=new Account(null,username,password,email,"user",new Date());
        
        // 保存账户到数据库
        if(this.save(account)){
            stringRedisTemplate.delete(key);  // 注册成功后删除验证码
            return null;  // 注册成功
        }else{
            return "内部错误，请联系管理员";
        }
    }
    /**
     * 重置邮箱账户密码实现
     * 验证验证码后更新用户密码
     * 
     * @param vo 重置密码请求参数
     * @return 重置结果消息，成功返回null，失败返回错误信息
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email=vo.getEmail();
        
        // 先验证验证码
        String verify=this.resetConfirm(new ConfirmResetVO( email, vo.getCode()));
        if(verify!=null) return verify;
        
        // 加密新密码并更新数据库
        String password=encoder.encode(vo.getPassword());
        boolean update=this.update().eq("email", email).set("password",password).update();
        
        // 更新成功后删除验证码缓存
        if( update){
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
        }
        return null;
    }
    
    /**
     * 确认重置密码验证
     * 验证邮箱和验证码是否匹配
     * 
     * @param vo 确认重置请求参数
     * @return 验证结果消息，成功返回null，失败返回错误信息
     */
    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email=vo.getEmail();
        
        // 从Redis获取验证码
        String code=stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        if(code==null) return "请先获取验证码";
        
        // 验证验证码是否正确
        if(!code.equals(vo.getCode())) return "验证码错误";
        return null;
    }
    
    /**
     * 注册邮箱验证码发送实现
     * 生成验证码并通过RabbitMQ发送邮件，同时缓存到Redis
     * 
     * @param type 验证码类型（register/reset）
     * @param email 邮箱地址
     * @param ip 请求IP地址
     * @return 发送结果消息，成功返回null，失败返回错误信息
     */
    @Override
    public String registerEmailVerifyCode(String type,String email,String ip) {
        // 使用IP地址作为同步锁，防止同一IP频繁请求
        synchronized (ip.intern()){
            // 检查请求频率限制
            if(!this.verifyLimit(ip))
                return "请求频繁，请稍后再试";
                
            // 生成6位随机验证码
            Random random=new Random();
            int code=random.nextInt(899999)+100000;
            
            // 构建邮件数据
            Map<String,Object>data= Map.of(
                    "type",type,
                    "email",email,
                    "code",code
            );
            
            // 发送邮件消息到RabbitMQ队列
            amqpTemplate.convertAndSend("mail",data);
            
            // 将验证码缓存到Redis，有效期3分钟
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA+email,String.valueOf(code),3, TimeUnit.MINUTES);
        }
        return null;
    }

    /**
     * 检查邮箱是否已存在
     * 
     * @param email 邮箱地址
     * @return 邮箱已存在返回true，否则返回false
     */
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.< Account>query().eq("email", email));
    }
    
    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return 用户名已存在返回true，否则返回false
     */
    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.< Account>query().eq("username", username));
    }
    
    /**
     * 根据用户名查找用户账户信息
     * 使用MyBatis-Plus进行数据库查询
     * 
     * @param text 用户名
     * @return Account 用户账户对象，如果不存在返回null
     */
    public Account findAccountByUsername(String text) {
        return this.query()
                .eq("username", text)  // 查询条件：username字段等于传入的用户名
                .one();  // 返回单个结果
    }
    
    /**
     * 验证请求频率限制
     * 防止同一IP频繁请求验证码
     * 
     * @param ip 请求IP地址
     * @return 是否允许请求，允许返回true，否则返回false
     */
    private boolean verifyLimit(String ip){
        String key=Const.VERIFY_EMAIL_LIMIT+ip;
        return utils.limitOnceCheck(key,60);  // 60秒内只能请求一次
    }
}