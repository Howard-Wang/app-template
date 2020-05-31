package com.company.app.web.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.app.service.LoggerService;
import com.company.app.utils.HeaderUtil;
import com.company.app.utils.PaginationUtil;
import com.company.app.domain.WebLogTask;
import com.company.app.dto.WebLogTaskDTO;
import com.company.app.exception.GeneralException;
import com.company.app.exception.InvalidParamException;
import com.company.app.service.LoganService;
import com.company.app.service.WebLogParser;
import com.company.app.utils.ResourceUtil;
import com.company.app.utils.TypeSafeUtil;
import com.company.app.vm.LoggerVm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志信息接口
 * @author YunJ
 */
@Api(value="日志信息接口", tags = {"日志信息接口"})
@RestController
@RequestMapping("/api/logs")
public class LoggerResource {
    private final Logger logger = LoggerFactory.getLogger(LoggerResource.class);

    private static final String REGEX = ",";

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private LoganService loganService;

    @ApiOperation(value="获取服务端包内各个类的日志等级")
    @GetMapping(value = "/level")
    public List<LoggerVm> getList() {
        return loggerService.getLogLevel();
    }

    @ApiOperation(value="修改服务端包内各个类的日志等级")
    @PutMapping(value = "/level")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeLevel(@RequestBody LoggerVm jsonLogger) {
        loggerService.updateLogLevel(jsonLogger);
    }

    @ApiOperation(value="查看日志文件名称")
    @GetMapping(path = "/files")
    public ResponseEntity<?> getLogFiles() {
        return ResponseEntity.ok(loggerService.getLogFiles());
    }

    @ApiOperation(value="下载日志文件", notes="下载指定名称的日志文件")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "filename", value = "文件名", required = true, dataType = "String")
    })
    @GetMapping(path = "/files/download")
    public ResponseEntity<Resource> downloadLog(@RequestParam("filename") String filename) {
        return ResponseEntity.ok().headers(HeaderUtil.getDownloadHeader(filename))
            .contentType(MediaType.parseMediaType("application/x-msdownload"))
            .body(loggerService.getResource(filename));
    }

    @ApiOperation(value="获取当前服务端日志文件的内容")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "bytesCount", value = "字节数", required = false, dataType = "int")
    })
    @GetMapping(value = "/server-log")
    public ResponseEntity<?> getClientLog(@RequestParam(value = "bytesCount", required = false, defaultValue = "0") int size) {
        if (size <= 0) {
            throw new InvalidParamException();
        }
        return ResponseEntity.ok(loggerService.getLogContent(size));
    }

    @ApiOperation(value="web 端日志上传")
    @PostMapping("/web/upload")
    @ResponseBody
    public ResponseEntity<?> upload(@RequestBody WebLogTaskDTO taskModel) {
        if (taskModel == null || !taskModel.isValid()) {
            throw new InvalidParamException();
        }
        try {
            taskModel.setContent(WebLogParser.parse(taskModel.getLogArray()));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException();
        }
        if (!loganService.saveTask(taskModel)) {
            throw new InvalidParamException();
        }
        // 这里为了适应前端的 logan 插件，必须要返回 code 200
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", 200);
        map.put("msg", "success");
        return ResponseEntity.ok(map);
    }

    @ApiOperation(value="根据详情ID查询单条日志详情")
    @GetMapping("/web/{detailId}")
    @ResponseBody
    public ResponseEntity<WebLogTask> logDetail(@PathVariable String detailId) {
        long longDetailId = NumberUtils.toLong(detailId);
        WebLogTask detailDTO = loganService.queryById(longDetailId);
        if (detailDTO == null) {
            throw new InvalidParamException();
        }
        return ResponseEntity.ok(detailDTO);
    }

    @ApiOperation(value="删除指定 id 的日志")
    @DeleteMapping("/web-logs")
    public ResponseEntity<?> deleteUser(String tasks) {
        if (StringUtils.isEmpty(tasks)) {
            throw new InvalidParamException("tasks");
        }
        List<Long> taskIds = TypeSafeUtil.parseLongList(tasks, REGEX);
        taskIds = TypeSafeUtil.ignore(taskIds, 0L);
        loganService.deleteByTaskIds(taskIds);
        return ResponseEntity.ok(ResourceUtil.success());
    }

    /**
     * 按天搜索某个deviceId的日志
     *
     * @param beginTime 时间戳类型（单位：毫秒），一天的起始时间，如: 2019-11-11 00:00:00，不能为空
     * @param endTime    时间戳类型（单位：毫秒），一天的起始时间，如: 2019-11-11 00:00:00，不能为空
     * @param deviceId  设备ID，不能为空
     * @return deviceId在一天内上报的列表
     */
    @ApiOperation(value="按天搜索某个deviceId的日志")
    @GetMapping("/web-logs")
    @ResponseBody
    public ResponseEntity<List<WebLogTask>> search(@RequestParam(required = false) Long beginTime,
                                                   @RequestParam(required = false) Long endTime,
                                                   @RequestParam(required = false) String deviceId,
                                                   @RequestParam(required = false) String webSource,
                                                   @RequestParam(required = false) String environment,
                                                   @RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) String content,
                                                   @RequestParam(required = false) Long logDate,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) String customReportInfo,
                                                   Pageable pageable) {
        LambdaQueryWrapper<WebLogTask> queryWrapper = new LambdaQueryWrapper<>();
        if (beginTime != null && beginTime > 0) {
            queryWrapper.gt(WebLogTask::getAddTime, beginTime);
        }
        if (endTime != null && endTime > 0) {
            queryWrapper.lt(WebLogTask::getAddTime, endTime);
        }
        if (StringUtils.isNotEmpty(deviceId)) {
            queryWrapper.eq(WebLogTask::getDeviceId, deviceId);
        }
        if (StringUtils.isNotEmpty(webSource)) {
            queryWrapper.eq(WebLogTask::getWebSource, webSource);
        }
        if (StringUtils.isNotEmpty(environment)) {
            queryWrapper.eq(WebLogTask::getEnvironment, environment);
        }
        if (pageNum != null) {
            queryWrapper.eq(WebLogTask::getPageNum, pageNum);
        }
        if (logDate != null) {
            queryWrapper.eq(WebLogTask::getLogDate, logDate);
        }
        if (status != null) {
            queryWrapper.eq(WebLogTask::getStatus, status);
        }
        if (StringUtils.isNotEmpty(customReportInfo)) {
            queryWrapper.like(WebLogTask::getCustomReportInfo, customReportInfo);
        }
        if (StringUtils.isNotEmpty(content)) {
            queryWrapper.like(WebLogTask::getContent, content);
        }
        Page<WebLogTask> page = loganService.search(queryWrapper, PaginationUtil.getPage(pageable));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/logs/web-logs");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    @ApiOperation(value="以文件的方式下载原始日志")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "tasks", value = "task id", required = true, dataType = "String")
    })
    @GetMapping("/web/download")
    public ResponseEntity<byte[]> downLoadLog(@RequestParam("tasks") String tasks){
        if (StringUtils.isEmpty(tasks)) {
            throw new InvalidParamException("tasks");
        }
        List<Long> taskIds = TypeSafeUtil.parseLongList(tasks, REGEX);
        List<WebLogTask> taskDtos = loganService.queryByTaskIds(taskIds);
        byte[] contents = loganService.getContentAsBytes(taskDtos);

        String filename = "web_log_" + System.currentTimeMillis() + ".log";
        return ResponseEntity.ok().headers(HeaderUtil.getDownloadHeader(filename))
            .contentType(MediaType.parseMediaType("application/x-msdownload"))
            .body(contents);
    }
}
