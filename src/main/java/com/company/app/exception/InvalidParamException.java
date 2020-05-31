package com.company.app.exception;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 非法参数 exception 处理
 * @author YunJ
 */
public class InvalidParamException extends RuntimeException  {

    public InvalidParamException() {
        super();
    }

    public InvalidParamException(String msg) {
        super(msg);
    }

    public InvalidParamException(List<String> errorArgs) {
        String msg = "";
        if (CollectionUtils.isNotEmpty(errorArgs)) {
            msg = String.join(",", errorArgs);
        }
        throw new InvalidParamException(msg);
    }
}
