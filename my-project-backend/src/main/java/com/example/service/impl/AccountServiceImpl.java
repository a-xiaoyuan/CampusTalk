package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 实现Spring Security的UserDetailsService接口，用于用户认证
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

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
}