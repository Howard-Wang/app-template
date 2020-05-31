package com.company.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Header 工具类
 * @author YunJ
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createDownloadHttpHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        return headers;
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-human-alert", message);
        headers.add("X-human-params", param);
        return headers;
    }

    public static HttpHeaders createErrorAlert(String code, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-human-error-code", code);
        headers.add("X-human-error-message", message);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity processing failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-human-error", defaultMessage);
        headers.add("X-human-params", entityName);
        return headers;
    }

    public static HttpHeaders getDownloadHeader(String clientPath) {
        String[] path = clientPath.split("\\/");
        if (path.length > 1) {
            clientPath = path[path.length - 1];
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.add("Content-Disposition", "attachment;filename=\"" + clientPath + "\"");
        return headers;
    }
}
