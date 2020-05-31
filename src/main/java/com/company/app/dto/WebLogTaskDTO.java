package com.company.app.dto;

import com.company.app.domain.WebLogTask;
import com.company.app.enums.TrimFieldEnum;
import com.company.app.utils.DateTimeUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * web 日志任务
 * @author YunJ
 */
@Data
public class WebLogTaskDTO {

    private String deviceId;

    private Integer logPageNo;

    private String webSource;

    private String environment;

    private String logArray;

    private transient String content;

    private transient long logDate;

    private String fileDate;

    private String client;

    private String customInfo;

    public boolean isValid() {
        if (StringUtils.isEmpty(deviceId) || logPageNo <= 0 || StringUtils.isEmpty(logArray)
            || StringUtils.isEmpty(fileDate)) {
            return false;
        }

        Date date = DateTimeUtil.parseDate(fileDate);
        if (date == null) {
            return false;
        } else {
            logDate = DateTimeUtil.trimAfter(TrimFieldEnum.HOUR, date).getTime();
        }
        return true;
    }

    public WebLogTask transformToWebLogTask() {
        WebLogTask taskDTO = new WebLogTask();
        taskDTO.setDeviceId(deviceId);
        taskDTO.setWebSource(webSource);
        taskDTO.setEnvironment(environment);
        taskDTO.setPageNum(logPageNo);
        taskDTO.setContent(content);
        taskDTO.setLogDate(logDate);
        taskDTO.setCustomReportInfo(customInfo);
        return taskDTO;
    }
}
