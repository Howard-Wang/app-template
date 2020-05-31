package com.company.app.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 上传的附件信息
 * @author YunJ
 */
@Data
public class Attachment {

    @TableId
    private String id;

    private String fileUrl;
    private String filename;
    private Long fileSize;
    private String filePath;

    /**
     * 附件状态：未下载，下载成功，下载失败
     */
    private String status;

    private Date createdTime = new Date();
}
