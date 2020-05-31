package com.company.app.security;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 跨域配置
 * @author YunJ
 */
@Slf4j
public class WebSecurityCorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String originHeader = req.getHeader("Origin");

        if (null != originHeader) {
            res.setHeader("Access-Control-Allow-Origin", originHeader);
            log.debug("url: {}, Origin -- {}", req.getRequestURI(), originHeader);
        }

        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        /**
         * 增加这项的目的是为了让前端在跨域访问的时候可以获取到 headers 中的 Link 否则默认暴露的 header 中没有 Link
         * 参考 https://stackoverflow.com/questions/37897523/axios-get-access-to-response-header-fields
         */
        res.setHeader("Access-Control-Expose-Headers", "Link, X-human-error-message, X-Total-Count, Content-Disposition, Authorization");
        res.setHeader("Access-Control-Allow-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, x-requested-with, Cache-Control");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, res);
    }
}
