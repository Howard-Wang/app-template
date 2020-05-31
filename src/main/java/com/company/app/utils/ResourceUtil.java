package com.company.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 资源工具类
 * @author YunJ
 */
public class ResourceUtil {
    private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

    /**
     * 返回成功消息
     */
    public static Map<String, String> success() {
        Map<String, String> map = new HashMap<>(2);
        map.put("code", "0");
        map.put("msg", "success");
        return map;
    }

    /**
     * 获取返回给前端的错误信息
     */
    public static String getErrorMessage(String code) {
        String paramDesc = ResourceUtil.transformMessageByLocale(code);
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", code);
        map.put("desc", paramDesc);

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = "";
        try {
            msg = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("getErrorMessage failed");
        }
        return msg;
    }

    public static String getErrorMessage(String code, String args) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", code);

        String paramDesc = ResourceUtil.transformMessageByLocale(code);
        if (!StringUtils.isEmpty(args)) {
            paramDesc = paramDesc + " : " + args;
            map.put("args", TypeSafeUtil.parseStringList(args));
        }
        map.put("desc", paramDesc);

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = "";
        try {
            msg = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("getErrorMessage failed");
        }
        return msg;
    }

    /**
     * 将字符串改转国际化
     */
    public static String transformMessageByLocale(String param) {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        MessageSource messageSource = ResourceUtil.getMessageSource();
        return messageSource.getMessage(param, null, "", locale);
    }

    /**
     * 国际化
     */
    public static ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setBasenames("/i18n/messages");
        return messageSource;
    }

    /**
     * 密码加密
     */
    public static String encodePwd(String str) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(str);
    }
}
