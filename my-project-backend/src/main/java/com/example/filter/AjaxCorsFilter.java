package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CORS跨域过滤器
 * 处理跨域请求，允许前端应用访问后端API
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Component  // Spring组件注解
@Order(Const.ORDER_CORS)  // 过滤器执行顺序
public class AjaxCorsFilter extends HttpFilter {
    
    /**
     * 过滤器核心方法
     * 为每个请求添加CORS头信息，支持跨域访问
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param chain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilter(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        this.addCorsHeaders(request,response);  // 添加CORS头信息
        chain.doFilter(request, response);  // 继续过滤器链
    }

    /**
     * 添加CORS头信息
     * 配置跨域访问的允许来源、方法和头信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    private void addCorsHeaders(HttpServletRequest request,
                                HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));  // 允许的来源
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");  // 允许的HTTP方法
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");  // 允许的头信息
    }
}
