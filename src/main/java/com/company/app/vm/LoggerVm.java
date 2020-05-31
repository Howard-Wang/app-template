package com.company.app.vm;

import ch.qos.logback.classic.Logger;
import lombok.Data;

/**
 * 日志等级
 * @author YunJ
 */
@Data
public class LoggerVm {
    private String name;
    private String level;

    public LoggerVm(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    public LoggerVm() {
        // Empty public constructor used by Jackson.
    }

    @Override
    public String toString() {
        return "LoggerVM {" + "name='" + name + "'" + ", level='" + level + "'}";
    }
}
