// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name SnapshotAddressUtils.java.
// File size 1687.
// File create date Fri Jul 12 12:55:21 CST 2024.
// File update date Fri Jul 12 12:55:21 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * StrategyHttpUtil
 *
 * @author admin
 * @since 2024/06/22 17:05
 */
@Slf4j
public class StrategyHttpUtil {

    /**
     * POST请求，json格式
     *
     * @author erZhu
     * @date 2024/6/22 17:07
     */
    public static boolean post(String path, String content) {
        log.info("StrategyHttpUtil.post start, url=[{}], content=[{}]", path, content);
        try {
            // URL对象
            URL url = new URL(path);
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            // 允许发送输出流
            connection.setDoOutput(true);
            // 发送JSON数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = content.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (200 != responseCode){
                log.error("StrategyHttpUtil.post error, url=[{}], content=[{}], responseCode=[{}]",
                        path, content, responseCode);
                return false;
            }
        } catch (Exception e) {
            log.error("StrategyHttpUtil.post error, url=[{}], content=[{}]", path, content, e);
            return false;
        }
        return true;
    }
}
