package com.company.app.exception;

/**
 * 一般性异常
 * 不需要单独自定义的 Exception 可以使用该异常
 * @author YunJ
 */
public class GeneralException extends RuntimeException {

    public GeneralException() {
        super();
    }

    /**
     * @param message 是 ErrorCode 中的错误码
     */
    public GeneralException(String message) {
        super(message);
    }
}
