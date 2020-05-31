package com.company.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.app.enums.WebLogFieldEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * web log 日志解析
 * @author YunJ
 */
public class WebLogParser {

    public static String parse(String logArray) throws UnsupportedEncodingException {
        List<String> logs = new LinkedList<>();
        String[] logItems = logArray.split(",");
        for (String item : logItems) {
            String content = URLDecoder.decode(item, "UTF-8");
            JSONObject jsonObject = JSON.parseObject(content);
            int version = (int) jsonObject.getOrDefault(WebLogFieldEnum.VERSION.key, 0);
            if (version == 0) {
                parseByVersion0(jsonObject, logs);
            } else if (version == 1) {
                parseByVersion1(jsonObject, logs);
            }
        }
        return JSON.toJSONString(logs);
    }

    private static void parseByVersion0(JSONObject jsonObject, List<String> logs) {
        String log = jsonObject.getString(WebLogFieldEnum.LOG.key);
        if (StringUtils.isNotEmpty(log)) {
            logs.add(new String(Base64.decodeBase64(log)));
        }
    }

    private static void parseByVersion1(JSONObject jsonObject, List<String> logs) {
        byte[] log = Base64.decodeBase64(jsonObject.getString(WebLogFieldEnum.LOG.key));
        String iv = jsonObject.getString(WebLogFieldEnum.IV.key);
        String secretKey = jsonObject.getString(WebLogFieldEnum.KEY.key);
        if (isAllNotEmpty(iv, secretKey) && log != null) {
            String content = decryptContent(log, iv, secretKey);
            if (content != null) {
                logs.add(content);
            }
        }
    }

    public static boolean isAllNotEmpty(String... strings) {
        for (String item : strings) {
            if (StringUtils.isEmpty(item)) {
                return false;
            }
        }
        return true;
    }

    private static String decryptContent(byte[] log, String iv, String secretKey) {
        return WebLogDecryptHelper.create(secretKey).doDecrypt(iv, log);
    }

    public static int getDayOffset(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return (int) (date.getTime() - calendar.getTime().getTime()) / (1000 * 60);
    }

}
