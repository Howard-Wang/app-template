package com.company.app.exception;

/**
 * 用户已经存在
 * @author YunJ
 */
public class UserExistException extends RuntimeException  {

    public UserExistException() {
        super();
    }

    public UserExistException(String msg) {
        super(msg);
    }
}
