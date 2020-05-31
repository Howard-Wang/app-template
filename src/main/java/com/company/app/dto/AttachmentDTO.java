package com.company.app.dto;

import com.company.app.constants.AttachmentConstants;
import com.company.app.domain.Attachment;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 附件
 * @author YunJ
 */
@Data
public class AttachmentDTO {

    @NotEmpty
    private String fileUrl;

    @NotEmpty
    private String filename;

    public Attachment transformToAttachment() {
        Attachment attachment = new Attachment();
        attachment.setFileUrl(fileUrl);
        attachment.setFilename(filename);
        attachment.setStatus(AttachmentConstants.ATTACHMENT_STATUS_INTI);
        return attachment;
    }
}
