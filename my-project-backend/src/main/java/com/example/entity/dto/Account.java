package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 账户实体类
 * 对应数据库中的db_account表，存储用户账户信息
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Data  // Lombok注解，自动生成getter/setter等方法
@TableName("db_account")  // MyBatis-Plus注解，指定数据库表名
@AllArgsConstructor  // 生成全参构造器
@NoArgsConstructor   // 生成无参构造器
public class Account implements BaseData {
    
    /**
     * 用户ID，主键，自增
     */
    @TableId(type= IdType.AUTO)
    Integer id;
    
    /**
     * 用户名
     */
    String username;
    
    /**
     * 密码（加密存储）
     */
    String password;
    
    /**
     * 邮箱地址
     */
    String email;
    
    /**
     * 用户角色
     */
    String role;
    
    /**
     * 注册时间
     */
    Date registerTime;
}
