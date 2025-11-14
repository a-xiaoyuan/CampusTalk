package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
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
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    FlowUtils utils;
    @Resource
    PasswordEncoder encoder;
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
    public String registerEmailAccount(EmailRegisterVO vo){
        String email=vo.getEmail();
        String username=vo.getUsername();
        String key=Const.VERIFY_EMAIL_DATA+email;
        String code=stringRedisTemplate.opsForValue().get(key);
        if(code==null) return "请先获取验证码";
        if(!code.equals(vo.getCode()))
            return "验证码错误";
        if(this.existsAccountByEmail(email))
            return "邮箱已存在";
        if(this.existsAccountByUsername(username))
            return "用户名已存在";
        String password=encoder.encode(vo.getPassword());
        Account account=new Account(null,username,password,email,"user",new Date());
        if(this.save(account)){
            stringRedisTemplate.delete(key);
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
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
     *
     * @return Account 用户账户对象，如果不存在返回null
     */
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.< Account>query().eq("email", email));
    }
    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.< Account>query().eq("username", username));
    }
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