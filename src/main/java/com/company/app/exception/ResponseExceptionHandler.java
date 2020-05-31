package com.company.app.exception;

import com.company.app.constants.ErrorCode;
import com.company.app.utils.ResourceUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 异常处理
 * @author YunJ
 */
@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 处理参数异常的返回
     */
    @ExceptionHandler(value = InvalidParamException.class)
    protected ResponseEntity<Object> handleInvaildParamException(RuntimeException e, WebRequest webRequest) {
        String msg = ResourceUtil.getErrorMessage(ErrorCode.ERROR_INVALID_PARAM, e.getMessage());
        return handleExceptionInternal(e, msg,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest);
    }

    /**
     * 一般性错误
     */
    @ExceptionHandler(value = GeneralException.class)
    protected ResponseEntity<Object> handleGeneralException(RuntimeException e, WebRequest webRequest) {
        String msg = ResourceUtil.getErrorMessage(e.getMessage());
        return handleExceptionInternal(e, msg,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest);
    }

    /**
     * 用户不存在
     */
    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(RuntimeException e, WebRequest webRequest) {
        String msg = ResourceUtil.getErrorMessage(ErrorCode.ERROR_USER_NOT_FOUND);
        return handleExceptionInternal(e, msg,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest);
    }

    /**
     * 用户已经存在
     */
    @ExceptionHandler(value = UserExistException.class)
    protected ResponseEntity<Object> handleUserExistExceptionn(RuntimeException e, WebRequest webRequest) {
        String msg = ResourceUtil.getErrorMessage(ErrorCode.ERROR_USER_EXIST);
        return handleExceptionInternal(e, msg,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            webRequest);
    }
}
