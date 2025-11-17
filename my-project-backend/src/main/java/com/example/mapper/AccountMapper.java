package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Mapper  // MyBatis注解，标识为Mapper接口
public interface AccountMapper extends BaseMapper<Account> {
}
