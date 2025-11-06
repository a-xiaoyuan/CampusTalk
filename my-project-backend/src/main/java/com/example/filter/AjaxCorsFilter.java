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

@Component
@Order(Const.ORDER_CORS)
public class AjaxCorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        this.addCorsHeaders(request,response);
        chain.doFilter(request, response);
    }

    private void addCorsHeaders(HttpServletRequest request,
                                HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "Origin");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }
}
