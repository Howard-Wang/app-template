package com.company.app.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解, 主要用户 patch 资源信息
 * @author YunJ
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PatchRequestBody {
}
