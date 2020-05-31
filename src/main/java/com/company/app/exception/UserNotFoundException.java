package com.company.app.exception;

/**
 * 用户不存在
 * @author YunJ
 */
public class UserNotFoundException extends RuntimeException  {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
