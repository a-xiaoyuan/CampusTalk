package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 流量限制过滤器
 * 基于IP地址实现请求频率限制，防止恶意访问
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */
@Component  // Spring组件注解
@Order(Const.ORDER_FLOW_LIMIT)  // 过滤器执行顺序
public class FlowLimitFilter extends HttpFilter {
    
    /**
     * Redis模板，用于存储访问计数和限制信息
     */
    @Resource
    StringRedisTemplate template;
    
    /**
     * 过滤器核心方法
     * 检查IP地址的访问频率，超过限制则拒绝请求
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param chain 过滤器链
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();  // 获取客户端IP地址
        if(this.tryCount(address)){
            chain.doFilter(request,response);  // 允许通过
        }else{
            this.writeBlockMessage(response);  // 拒绝访问
        }
    }
    
    /**
     * 写入拒绝访问消息
     * 
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    private void writeBlockMessage(HttpServletResponse response) throws IOException{
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 设置403状态码
        response.setContentType("application/json;charset=utf-8");  // 设置响应类型
        response.getWriter().write(RestBean.forbidden("访问过于频繁，请稍后再试").asJsonString());  // 返回错误信息
    }
    
    /**
     * 尝试计数并检查是否允许访问
     * 
     * @param ip 客户端IP地址
     * @return 是否允许访问
     */
    private boolean tryCount(String ip){
        synchronized (ip.intern()){  // 使用字符串池同步，确保同一IP的并发安全
            if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK+ip)))  // 检查是否在阻塞列表中
                return false;
            return this.limitPeriodCheck(ip);  // 执行周期检查
        }
    }
    
    /**
     * 周期限制检查
     * 实现滑动窗口限流算法
     * 
     * @param ip 客户端IP地址
     * @return 是否允许访问
     */
    private boolean limitPeriodCheck(String ip){
        if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER+ip))) {
            // 已有计数器，增加计数
            long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
            if (increment > 10) {
                // 超过10次访问，加入阻塞列表30秒
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 30, TimeUnit.SECONDS);
                return false;
            }
        } else {
            // 首次访问，创建计数器，3秒过期
            template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }
}
