package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类
 * 提供JWT令牌的创建、验证、解析和失效管理功能
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtUtils {
    
    /**
     * JWT密钥，从配置文件读取
     */
    @Value("${spring.security.jwt.key}")
    String key;
    
    /**
     * JWT过期时间（天数），从配置文件读取
     */
    @Value("${spring.security.jwt.expire}")
    int expire;
    
    /**
     * Redis模板，用于JWT黑名单管理
     */
    @Resource
    StringRedisTemplate template;
    /**
     * 解析JWT令牌
     * 验证令牌有效性并检查是否在黑名单中
     * 
     * @param headerToken HTTP头中的Bearer令牌
     * @return 解析后的JWT对象，无效返回null
     */
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);  // 提取令牌
        if (token == null)return null;
        
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        
        try{
            DecodedJWT verify = verifier.verify(token);  // 验证令牌签名
            
            // 检查令牌是否在黑名单中
            if(this.isInvalidToken(verify.getId()))
                return null;
                
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt)?null:verify;  // 检查是否过期
        }catch (JWTVerificationException e){
            return null;
        }
    }

    /**
     * 创建JWT令牌
     * 
     * @param details 用户详情
     * @param id 用户ID
     * @param username 用户名
     * @return 生成的JWT令牌字符串
     */
    public String createJwt(UserDetails details, int id, String username){
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())  // 唯一标识
                .withClaim("id",id)  // 用户ID
                .withClaim("name", username)  // 用户名
                .withClaim("authorizes",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())  // 权限列表
                .withExpiresAt(expire)  // 过期时间
                .withIssuedAt(new Date())  // 签发时间
                .sign(algorithm);  // 签名
    }
    
    /**
     * 计算过期时间
     * 
     * @return 过期时间点
     */
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);  // 转换为小时
        return calendar.getTime();
    }
    
    /**
     * 转换HTTP头令牌
     * 从"Bearer token"格式中提取令牌
     * 
     * @param headerToken HTTP头中的令牌
     * @return 纯令牌字符串，无效返回null
     */
    private String convertToken(String headerToken){
        if (headerToken == null || !headerToken.startsWith("Bearer "))return null;
        return headerToken.substring(7);  // 去掉"Bearer "前缀
    }
    
    /**
     * 从JWT中提取用户信息
     * 
     * @param jwt JWT对象
     * @return Spring Security用户详情对象
     */
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())  // 用户名
                .password("******")  // 密码占位符
                .authorities(claims.get("authorizes").asArray(String.class))  // 权限
                .build();
    }
    
    /**
     * 从JWT中提取用户ID
     * 
     * @param jwt JWT对象
     * @return 用户ID
     */
    public Integer toId(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    /**
     * 使JWT令牌失效
     * 将令牌加入黑名单
     * 
     * @param headerToken HTTP头中的令牌
     * @return 是否成功失效
     */
    public boolean invalidateJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if(token==null) return false;
        
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        
        try{
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());  // 加入黑名单
        }catch(JWTVerificationException e){
            return false;
        }
    }

    /**
     * 删除令牌（加入黑名单）
     * 
     * @param uuid 令牌ID
     * @param time 过期时间
     * @return 是否成功删除
     */
    private boolean deleteToken(String uuid, Date time) {
        if(this.isInvalidToken(uuid))  // 已存在黑名单中
            return false;
            
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(),0);  // 剩余过期时间
        
        // 将令牌加入黑名单，有效期到令牌自然过期
        template.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }
    
    /**
     * 检查令牌是否在黑名单中
     * 
     * @param uuid 令牌ID
     * @return 是否在黑名单中
     */
    private boolean isInvalidToken(String uuid) {
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

}