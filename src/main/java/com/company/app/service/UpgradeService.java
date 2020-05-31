package com.company.app.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;

/**
 * 项目启动就调用相关的方法
 * 主要解决业务升级
 * @author YunJ
 */
@Service
@Slf4j
public class UpgradeService {
    private final Logger logger = LoggerFactory.getLogger(UpgradeService.class);

    @Autowired
    private SettingService settingService;

    @Autowired
    DataSource dataSource;

    @Transactional(rollbackFor = Exception.class)
    public void upgrade() throws Exception {
        log.info("业务数据版本检查开始，请稍后...");
        // 检查配置是否需要升级V1
        String oldVersion = settingService.getDbVersion();
        if (StringUtils.isEmpty(oldVersion)) {
            oldVersion = "1";
        }

        // 新版本号
        int newVersion = Integer.parseInt(oldVersion);
        switch (oldVersion) {
            case "1":
                this.execSqlFile(newVersion);
                newVersion++;
            case "2":
                // TODO: 以后升级,可以在此处写代码，调用
            default:
                break;
        }

        // 保存数据版本号
        settingService.updateDbVersion(String.valueOf(newVersion));
        log.info("业务数据版本检查结束");
    }

    public void execSqlFile(int version) throws Exception {
        ScriptRunner runner = null;
        Reader reader = null;
        InputStream in = null;
        try {
            logger.info("V" + version + " sql upgrade staring...");

            // 使用sql脚本的方式升级
            Connection connection = dataSource.getConnection();
            runner = new ScriptRunner(connection);
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:db/upgradeV" + version + ".sql");
            in = resource.getInputStream();
            reader = new InputStreamReader(in);
            runner.setStopOnError(true);
            runner.runScript(reader);
            logger.info("V" + version + " sql upgrade end");
        } catch (Exception e) {
            logger.error("V" + version + " Upgrade failed, waiting for the next start to automatically upgrade:", e);
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (in != null) {
                in.close();
            }
            if (runner != null) {
                runner.closeConnection();
            }
        }
    }
}
