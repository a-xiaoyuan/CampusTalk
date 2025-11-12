package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 实现Spring Security的UserDetailsService接口，用于用户认证
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    FlowUtils utils;

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
    @Override
    public String registerEmailVerifyCode(String type,String email,String ip) {
        synchronized (ip.intern()){
            if(!this.verifyLimit(ip))
                return "请求频繁，请稍后再试";
            Random random=new Random();
            int code=random.nextInt(899999)+100000;
            Map<String,Object>data= Map.of(
                    "type",type,
                    "email",email,
                    "code",code
            );
            amqpTemplate.convertAndSend("mail",data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA+email,String.valueOf(code),3, TimeUnit.MINUTES);
        }
        return null;
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
    private boolean verifyLimit(String ip){
        String key=Const.VERIFY_EMAIL_LIMIT+ip;
        return utils.limitOnceCheck(key,60);
    }
}