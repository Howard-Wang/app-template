package com.company.app.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * logan 传入的日志
 * @author YunJ
 */
@Data
@TableName("webTask")
public class WebLogTask implements Comparable<WebLogTask> {

    @TableId("id")
    private String taskId;

    /**
     * 设备标示
     */
    private String deviceId;

    /**
     * 来源
     */
    private String webSource;

    /**
     * 环境信息
     */
    private String environment;

    private int pageNum;

    @JsonIgnore
    private String content;

    /**
     * 日志上报时间
     */
    private long addTime = System.currentTimeMillis();

    /**
     * 日志所属天
     */
    private long logDate;

    /**
     * 0 未解析过, 1 解析过
     */
    private int status = 0;

    /**
     * 客户端上报元信息
     */
    private String customReportInfo;

    private Date updateTime;

    @Override
    public int compareTo(WebLogTask o) {
        if (null == o) {
            return 0;
        }
        return this.pageNum - o.getPageNum();
    }
}
