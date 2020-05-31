package com.company.app.service;

import com.company.app.constants.SettingConstants;
import com.company.app.domain.Setting;
import com.company.app.mapper.SettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置
 * @author YunJ
 */
@Service
public class SettingService {
    private final Logger logger = LoggerFactory.getLogger(SettingService.class);

    @Autowired
    private SettingMapper settingMapper;

    /**
     * 获取数据库的版本号
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public String getDbVersion() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("name", SettingConstants.SETTING_DB_VERSION);
        List<Setting> settings = settingMapper.selectByMap(map);
        if (settings.isEmpty()) {
            return "";
        }
        return settings.get(0).getValue();
    }

    /**
     * 更新数据库版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDbVersion(String version) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("name", SettingConstants.SETTING_DB_VERSION);
        List<Setting> settings = settingMapper.selectByMap(map);
        if (!settings.isEmpty()) {
            Setting setting = settings.get(0);
            setting.setValue(version);
            settingMapper.updateById(setting);
        } else {
            Setting setting = new Setting();
            setting.setName(SettingConstants.SETTING_DB_VERSION);
            setting.setValue(version);
            settingMapper.insert(setting);
        }
    }
}
