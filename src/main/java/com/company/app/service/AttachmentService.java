package com.company.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.company.app.constants.ErrorCode;
import com.company.app.constants.AttachmentConstants;
import com.company.app.domain.Attachment;
import com.company.app.dto.AttachmentDTO;
import com.company.app.exception.GeneralException;
import com.company.app.exception.InvalidParamException;
import com.company.app.mapper.AttachmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附件
 * @author YunJ
 */
@Service
@Slf4j
public class AttachmentService {

    @Value("${customize.file.path}")
    private String filePath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private AttachmentMapper attachmentMapper;

    public AttachmentService(AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    public Map<String, Boolean> save(List<AttachmentDTO> attachments) {
        Map<String, Boolean> result = new HashMap<>(attachments.size());
        for (AttachmentDTO attachment : attachments) {
            Attachment attach = attachment.transformToAttachment();
            Boolean sqlResult = SqlHelper.retBool(attachmentMapper.insert(attach));
            result.put(attachment.getFilename(), sqlResult);
        }
        return result;
    }

    public List<Attachment> get() {
        return attachmentMapper.selectList(null);
    }

    public String getFileName(String id) {
        Attachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            throw new InvalidParamException("id");
        }
        File fileDir = new File(filePath);
        if (!fileDir.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        String[] files = fileDir.list();
        if (files == null) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }

        for (String filename : files) {
            if (filename.contains(attachment.getFilename())) {
                return filename;
            }
        }
        return "";
    }

    /**
     * todo 还需要使用一个定时器，用作文件的拉取
     */
    @Async
    public void downloadFile() {
        File fileDir = new File(filePath);
        if (!fileDir.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        LambdaQueryWrapper<Attachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attachment::getStatus, AttachmentConstants.ATTACHMENT_STATUS_INTI)
            .or()
            .eq(Attachment::getStatus, AttachmentConstants.ATTACHMENT_STATUS_DOWNLOAD_FAIL);

        RestTemplate restTemplate;
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();
            HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(requestFactory);

            for (Attachment attachment : attachmentMapper.selectList(queryWrapper)) {
                String fileUrl = attachment.getFileUrl();
                File file = restTemplate.execute(fileUrl, HttpMethod.GET, null, clientHttpResponse -> {
                    String[] args = fileUrl.split("\\.");
                    String filename = attachment.getId() + "_" + attachment.getFilename();
                    File ret = new File(filename);
                    StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                    return ret;
                });
                if (file != null && file.isFile()) {
                    attachment.setFilePath(contextPath + "/api/staticFile/" + file.getName());
                    attachment.setStatus(AttachmentConstants.ATTACHMENT_STATUS_DOWNLOAD_SUCCESS);
                    attachment.setFileSize(file.length());
                } else {
                    attachment.setStatus(AttachmentConstants.ATTACHMENT_STATUS_DOWNLOAD_FAIL);
                }
                attachmentMapper.updateById(attachment);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
    }

    public void decryptByBase64(String base64, String fileName) {
        if (StringUtils.isEmpty(base64) && StringUtils.isEmpty(fileName)) {
            throw new InvalidParamException();
        }
        File fileDir = new File(filePath);
        if (!fileDir.isDirectory()) {
            throw new GeneralException(ErrorCode.ERROR_CONFIGURATION_FILE);
        }
        Attachment attachment = new Attachment();
        fileName = filePath + "/" + fileName;
        base64 = base64.replaceAll(" ", "");
        base64 = base64.replaceAll("(\r\n|\r|\n|\n\r)", "");
        try {
            Files.write(Paths.get(fileName), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);

            File file = new File(fileName);
            if (!file.exists()) {
                throw new GeneralException(ErrorCode.ERROR_LOGS_FILE_NOT_FOUND);
            }
            attachment.setFileUrl("");
            attachment.setFilename(file.getName());
            attachment.setFilePath(contextPath + "/api/staticFile/" + file.getName());
            attachment.setStatus(AttachmentConstants.ATTACHMENT_STATUS_DOWNLOAD_SUCCESS);
            attachment.setFileSize(file.length());
            attachmentMapper.insert(attachment);
        } catch (IOException e) {
            log.error("decryptByBase64 failed : {}", e);
        }
    }
}
