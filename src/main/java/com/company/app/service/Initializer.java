package com.company.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 程序启动后，执行的初始化操作
 * @author YunJ
 */
@Component
public class Initializer {
    private final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private UpgradeService upgradeService;

    @PostConstruct
    public void init() {
        logger.info(">>> Init");

        try {
            upgradeService.upgrade();
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }
}
