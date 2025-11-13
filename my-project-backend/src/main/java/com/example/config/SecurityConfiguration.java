package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Spring Security 安全配置类
 * 负责配置应用程序的安全策略、认证和授权规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Resource
    JwtUtils utils;  // JWT工具类，用于生成和验证token
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;  // JWT认证过滤器
    @Resource
    AccountService service;  // 用户服务，用于用户认证

    /**
     * 配置安全过滤器链
     * 定义URL访问规则、登录/登出配置、异常处理等
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 配置URL访问权限
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**","/error").permitAll()  // 认证相关接口允许匿名访问
                        .anyRequest().authenticated()  // 其他所有请求需要认证
                )
                // 配置表单登录
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")  // 登录处理URL
                        .usernameParameter("username")  // 用户名参数名
                        .passwordParameter("password")  // 密码参数名
                        .failureHandler(this::onAuthenticationFailure)  // 登录失败处理器
                        .successHandler(this::onAuthenticationSuccess)  // 登录成功处理器
                )
                // 配置登出
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")  // 登出URL
                        .logoutSuccessHandler(this::onLogoutSuccess)  // 登出成功处理器
                )
                // 配置异常处理
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)  // 未认证处理
                        .accessDeniedHandler(this::onAccessDeny)  // 权限不足处理
                )
                .csrf(AbstractHttpConfigurer::disable)  // 禁用CSRF保护（因为使用JWT）
                // 配置会话管理为无状态（使用JWT）
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加JWT过滤器在用户名密码认证过滤器之前
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 配置认证管理器
     * Spring Security会自动使用AccountService作为UserDetailsService
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置密码编码器
     * 使用BCrypt算法对密码进行加密和验证
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 权限不足时的处理（403错误）
     */
    private void onAccessDeny(HttpServletRequest request,
                              HttpServletResponse response,
                              AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(e.getMessage()).asJsonString());
    }

    /**
     * 未认证时的处理（401错误）
     */
    private void onUnauthorized(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    /**
     * 登出成功处理
     * 使JWT token失效
     */
    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (utils.invalidateJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        } else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }
    }

    /**
     * 登录成功处理
     * 生成JWT token并返回用户信息
     */
    private void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        User user = (User) authentication.getPrincipal();  // 获取认证成功的用户信息
        Account account = service.findAccountByUsername(user.getUsername());
        String token = utils.createJwt(user, account.getId(), account.getUsername());  // 生成JWT token
        
        // 构建返回给前端的响应数据
        AuthorizeVO vo = new AuthorizeVO();
        vo.setUsername(account.getUsername());
        vo.setRole(account.getRole());
        vo.setToken(token);
        vo.setExpire(utils.expireTime());
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    /**
     * 登录失败处理
     * 返回401状态码和错误信息
     */
    private void onAuthenticationFailure(HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
    }
}