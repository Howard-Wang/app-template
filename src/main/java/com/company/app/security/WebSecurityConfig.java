package com.company.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * security 配置
 * @author YunJ
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public WebSecurityConfig(
        TokenProvider tokenProvider,
        CorsFilter corsFilter,
        JwtAuthenticationEntryPoint authenticationErrorHandler,
        JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * Configure BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure paths and requests that should be ignored by Spring Security
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/admin/**")
            .antMatchers("/static/**")
            // 调试接口
            .antMatchers("/swagger-ui/**")
            .antMatchers("/swagger-resources/**")
            .antMatchers("/v2/api-docs/**")
            .antMatchers("/webjars/**")
            .antMatchers("/api/staticFile/**")
            .antMatchers("/h2-console/**");
    }

    /**
     * Configure security settings
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // we don't need CSRF because our token is invulnerable
            .csrf().disable()

            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationErrorHandler)
            .accessDeniedHandler(jwtAccessDeniedHandler)

            // enable h2-console
            .and()
            .headers()
            .frameOptions()
            .sameOrigin()

            // create no session
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()
            .antMatchers("/", "/*.{js,html,css}").permitAll()
            .antMatchers("/", "/exportResource/*.{xlsx,xls}").permitAll()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/logs/web/upload").permitAll()
            .antMatchers("/api/file/{id}").permitAll()
            .antMatchers("/api/file/base64/upload").permitAll()

            // FIXME: 在此处对项目中的 url 进行权限分配
            .antMatchers(
                "/api/logs/level",
                "/api/logs/files",
                "/api/logs/files/download",
                "/api/logs/server-log",
                "/api/enterprise/info",
                "/api/user/reset/{id}"
                // FIXME: security4 之后， 任何与角色相关的方法都会自动添加 ROLE_ 前缀
            ).hasAnyRole("SUPERMAN")

            .antMatchers(
                "/api/system/version",
                "/api/user/me",
                "/api/logs/web/download",
                "/api/logs/web-logs",
                "/api/file",
                "/api/file/upload"
            ).hasAnyRole("USER", "SUPERMAN")

            .anyRequest()
            .authenticated()
            .and()
            // TODO: 下面一行持续集成会修改该行, 勿动
            .addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class)
            .apply(securityConfigurerAdapter());
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(tokenProvider);
    }
}
