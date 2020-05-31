package com.company.app.web.rest;

import com.company.app.dto.AttachmentDTO;
import com.company.app.exception.InvalidParamException;
import com.company.app.service.AttachmentService;
import com.company.app.service.LoggerService;
import com.company.app.utils.HeaderUtil;
import com.company.app.utils.ResourceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 文件接口
 * @author YunJ
 */
@Api(value="文件接口", tags = {"文件接口"})
@RestController
@RequestMapping("/api/file")
public class FileResource {

    private final Logger logger = LoggerFactory.getLogger(FileResource.class);

    private AttachmentService attachmentService;

    private LoggerService loggerService;

    public FileResource(AttachmentService attachmentService, LoggerService loggerService) {
        this.attachmentService = attachmentService;
        this.loggerService = loggerService;
    }

    @ApiOperation(value="文件上传")
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> upload(@Valid @RequestBody List<AttachmentDTO> attachments) {
        if (attachments.isEmpty()) {
            throw new InvalidParamException();
        }
        Map<String, Boolean> result = attachmentService.save(attachments);
        attachmentService.downloadFile();
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value="获取文件信息")
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(attachmentService.get());
    }

    @ApiOperation(value="下载文件")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "id", value = "文件 id", required = true, dataType = "String")
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource> downloadLog(@PathVariable String id) {
        String filename = attachmentService.getFileName(id);
        return ResponseEntity.ok().headers(HeaderUtil.getDownloadHeader(filename))
            .contentType(MediaType.parseMediaType("application/x-msdownload"))
            .body(loggerService.getResource(filename));
    }

    @ApiOperation(value="base64 编码文件上传")
    @PostMapping("/base64/upload")
    @ResponseBody
    public ResponseEntity<?> upload(@RequestBody Map<String, String> map) {
        String content = map.get("content");
        String name = map.get("name");
        attachmentService.decryptByBase64(content, name);
        return ResponseEntity.ok(ResourceUtil.success());
    }
}
