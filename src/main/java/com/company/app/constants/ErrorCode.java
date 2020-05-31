package com.company.app.constants;

/**
 * 系统内部的错误编码
 * @author YunJ
 */
public class ErrorCode {

    /**
     * 未知错误
     */
    public final static String ERROR_UNKNOWN = "10001";

    /**
     * 传入参数无效
     */
    public final static String ERROR_INVALID_PARAM = "10002";

    /**
     * 配置文件有问题
     */
    public final static String ERROR_CONFIGURATION_FILE = "10004";

    /**
     * 日志文件不存在
     */
    public final static String ERROR_LOGS_FILE_NOT_FOUND = "10005";

    /**
     * 用户不存在
     */
    public final static String ERROR_USER_NOT_FOUND = "10006";

    /**
     * 用户已存在
     */
    public final static String ERROR_USER_EXIST = "10007";
}
