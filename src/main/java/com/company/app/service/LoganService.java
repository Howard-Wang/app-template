package com.company.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.app.domain.WebLogTask;
import com.company.app.dto.WebLogTaskDTO;
import com.google.common.collect.ArrayListMultimap;
import com.company.app.mapper.WebLogTaskMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Logan 日志框架相关方法
 * @author YunJ
 */
@Service
public class LoganService {

    private final Logger logger = LoggerFactory.getLogger(LoganService.class);

    private static final String REGEX = ",";

    private WebLogTaskMapper logTaskMapper;

    public LoganService(WebLogTaskMapper logTaskMapper) {
        this.logTaskMapper = logTaskMapper;
    }

    /**
     * 存储客户端日志
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTask(WebLogTaskDTO taskModel) {
        try {
            WebLogTask taskDTO = taskModel.transformToWebLogTask();
            QueryWrapper<WebLogTask> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                .eq(WebLogTask::getLogDate, taskDTO.getLogDate())
                .eq(WebLogTask::getDeviceId, taskDTO.getDeviceId())
                .eq(WebLogTask::getPageNum, taskDTO.getPageNum());
            WebLogTask exist = logTaskMapper.selectOne(queryWrapper);
            if (exist != null) {
                taskDTO.setTaskId(exist.getTaskId());
                logTaskMapper.updateById(taskDTO);
            } else {
                logTaskMapper.insert(taskDTO);
            }
            return true;
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    /**
     * 查询数据库中日志
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<WebLogTask> search(LambdaQueryWrapper<WebLogTask> queryWrapper, Page<WebLogTask> page) {
        return logTaskMapper.selectPage(page, queryWrapper);
    }

    public WebLogTask queryById(Long taskId) {
        return logTaskMapper.selectById(taskId);
    }

    public List<WebLogTask> queryByTaskIds(List<Long> taskIds) {
        return logTaskMapper.selectBatchIds(taskIds);
    }

    public void deleteByTaskIds(List<Long> taskIds) {
        logTaskMapper.deleteBatchIds(taskIds);
    }

    public static File getFile(String fileName) {
        String path = new File("").getAbsolutePath() + File.separator + "logfile" + File.separator;
        File file = new File(path + fileName);
        if (!path.equals(file.getParentFile().getAbsolutePath() + File.separator)) {
            return null;
        }
        return file;
    }

    /**
     * 获取 web log 内容
     */
    public byte[] getContentAsBytes(List<WebLogTask> taskDtos) {
        Collections.sort(taskDtos);
        List<byte[]> bytesList = new LinkedList<>();
        int totalLength = 0;
        for (WebLogTask taskDTO : taskDtos) {
            try {
                byte[] bytes = URLDecoder.decode(taskDTO.getContent(), "UTF-8").getBytes();
                bytesList.add(bytes);
                totalLength += bytes.length;
            } catch (UnsupportedEncodingException e) {
                logger.error("content = {} UnsupportedEncodingException", taskDTO.getContent());
            }
        }
        int index = 0;
        byte[] contents = new byte[totalLength];
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, contents, index, bytes.length);
            index += bytes.length;
        }
        return contents;
    }

    /**
     * 由于H5日志采取分页上报，因此需要根据日志做聚合
     */
    public ArrayListMultimap<Long, WebLogTask> groupByLogDate(List<WebLogTask> list) {
        ArrayListMultimap<Long, WebLogTask> multiMap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (WebLogTask dto : list) {
                multiMap.put(dto.getLogDate(), dto);
            }
        }
        return multiMap;
    }
}
