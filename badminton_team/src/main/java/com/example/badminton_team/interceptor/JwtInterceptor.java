package com.example.badminton_team.interceptor;

import com.example.badminton_team.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取 token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 3. 验证 token
        if (!StringUtils.hasText(token) || !jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token无效\"}");
            return false;
        }

        // 4. 解析 token，设置用户信息到请求属性
        Integer id = jwtUtil.getIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        request.setAttribute("id", id);
        request.setAttribute("role", role);
        if ("user".equals(role)) {
            request.setAttribute("uid", id);
        } else if ("merchant".equals(role)) {
            request.setAttribute("mid", id);
        } else if ("admin".equals(role)) {
            request.setAttribute("aid", id);
        }

        return true;
    }
}