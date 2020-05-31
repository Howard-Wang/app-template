package com.company.app.config;

import com.company.app.constants.ErrorCode;
import com.company.app.annotation.PartialUpdateArgumentResolver;
import com.company.app.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

/**
 * web mvc 的配置
 *
 * @author YunJ
 */
@Component
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${customize.log.path}")
    private String logPath;

    private ApplicationContext applicationContext;

    private PartialUpdateArgumentResolver partialUpdateArgumentResolver;

    public MyWebMvcConfigurer(ApplicationContext applicationContext, PartialUpdateArgumentResolver partialUpdateArgumentResolver) {
        this.applicationContext = applicationContext;
        this.partialUpdateArgumentResolver = partialUpdateArgumentResolver;
    }

    /**
     * Adds the patch argument resolver
     * 自定义实现 @Patch 时，需要解析参数
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(partialUpdateArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ApplicationHome h = new ApplicationHome(getClass());
        File jarFile = h.getSource();
        File fileDir = new File(logPath);
        if (!fileDir.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        String path = fileDir.getAbsolutePath();
        registry.addResourceHandler("/api/staticFile/**").addResourceLocations("file:" + path);
    }

    /**
     * TODO 正式发布的时候持续集成需要去掉此处
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 加入此处配置和 WebSecurityCorsFilter 配合使用，否则当 POST 请求发出的时候，会先发 OPTIONS 这时候会提示 403
        // 下面一行代码持续集成会修改, 勿动
        registry.addMapping("/**").allowCredentials(true).allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
    }
}
