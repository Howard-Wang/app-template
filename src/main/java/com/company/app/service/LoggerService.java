package com.company.app.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.company.app.constants.ErrorCode;
import com.company.app.exception.GeneralException;
import com.company.app.vm.LoggerVm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志
 * @author YunJ
 */
@Service
@Slf4j
public class LoggerService {
    @Value("${customize.log.path}")
    private String logPath;

    @Value("${customize.log.name}")
    private String logName;

    private static final String SLASH_SYMBOL = "/";

    /**
     * 获取日志等级
     */
    public List<LoggerVm> getLogLevel() {
        List<LoggerVm> loggerVms = new ArrayList<>();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : context.getLoggerList()) {
            loggerVms.add(new LoggerVm(logger));
        }
        return loggerVms;
    }

    public void updateLogLevel(LoggerVm jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }

    /**
     * 获取所有生成的日志
     */
    public List<String> getLogFiles() {
        List<String> logFiles = new ArrayList<>();
        File file = new File(logPath);
        if (!file.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        String[] files = file.list();
        if (files == null) {
            return logFiles;
        }
        for (String filename : files) {
            if (filename.startsWith(logName) && filename.endsWith("log")) {
                logFiles.add(filename);
            }
        }
        return logFiles;
    }

    /**
     * 获取日志文件资源
     */
    public Resource getResource(String name) {
        File file = new File(logPath);
        if (!file.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        String filename;

        if (logPath.endsWith(SLASH_SYMBOL)) {
            filename = logPath + name;
        } else {
            filename = logPath + "/" + name;
        }
        File client = new File(filename);
        if (!client.isFile()) {
            throw new GeneralException(ErrorCode.ERROR_LOGS_FILE_NOT_FOUND);
        }
        Resource resource;
        try {
            resource = new InputStreamResource(new ByteArrayInputStream(FileUtils.readFileToByteArray(client)));
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.ERROR_LOGS_FILE_NOT_FOUND);
        }
        return resource;
    }

    /**
     * 获取文件内容
     *
     * @param size 获取内容大小
     */
    public String getLogContent(int size) {
        String filename;
        if (logPath.endsWith(SLASH_SYMBOL)) {
            filename = logPath + logName + ".log";
        } else {
            filename = logPath + "/" + logName + ".log";
        }
        File file = new File(filename);
        if (!file.exists()) {
            throw new GeneralException(ErrorCode.ERROR_LOGS_FILE_NOT_FOUND);
        }
        long fileSize = file.length();
        long start;
        if (size >= fileSize) {
            start = 0;
            size = (int) fileSize;
        } else {
            start = fileSize - size;
        }
        byte[] tempStr = new byte[size];
        try (FileInputStream bis = new FileInputStream(file)) {
            long skip = bis.skip(start);
            int bufferBytes = bis.read(tempStr, 0, size);
            log.debug("skip {} bytes, buffer has {} bytes", skip, bufferBytes);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new String(tempStr);
    }
}
