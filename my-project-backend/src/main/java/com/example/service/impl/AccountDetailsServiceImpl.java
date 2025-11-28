package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails>implements AccountDetailsService {
    @Resource
    AccountService service;
    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById( id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAccountDetails(int id, DetailsSaveVO vo) {
        try {
            // 检查用户名是否已被其他用户使用
            Account account = service.findAccountByUsername(vo.getUsername());
            if (account != null && account.getId() != id) {
                return false;  // 用户名已被其他用户使用
            }
            
            // 更新用户表中的用户名
            service.update()
                    .eq("id", id)
                    .set("username", vo.getUsername())
                    .update();
            
            // 构建详细信息对象
            AccountDetails details = new AccountDetails(
                    id, vo.getGender(), vo.getPhone(), vo.getQq(), vo.getWx(), vo.getDesc()
            );
            
            // 使用saveOrUpdate方法处理保存或更新逻辑
            // 这个方法会根据id自动判断是保存还是更新
            this.saveOrUpdate(details);
            
            return true;
        } catch (Exception e) {
            // 记录异常信息
            log.error("保存用户详细信息失败: {}", e.getMessage(), e);
            // 事务会自动回滚
            return false;
        }
    }
}
