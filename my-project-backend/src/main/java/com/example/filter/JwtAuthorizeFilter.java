package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 负责在每个请求中验证JWT令牌并设置用户认证信息
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Component  // Spring组件注解
public class JwtAuthorizeFilter extends OncePerRequestFilter {
    
    /**
     * JWT工具类，用于解析和验证JWT令牌
     */
    @Resource
    JwtUtils utils;
    
    /**
     * 过滤器核心方法
     * 在每个请求中验证JWT令牌，如果有效则设置用户认证信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");  // 获取Authorization头
        DecodedJWT jwt = utils.resolveJwt(authorization);  // 解析JWT令牌
        
        if(jwt!=null){
            UserDetails user=utils.toUser(jwt);  // 从JWT中提取用户信息
            
            // 创建认证令牌并设置权限
            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails( request));  // 设置认证详情
            SecurityContextHolder.getContext().setAuthentication(authentication);  // 设置安全上下文
            request.setAttribute("userId",utils.toId(jwt));  // 在请求中设置用户ID属性
        }
        
        filterChain.doFilter(request,response);  // 继续过滤器链
    }
}
