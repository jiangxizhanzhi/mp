 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseHttpService.java.
 // File size 2620.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.log.model.BaseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName BaseHttpService
 * @Description 基础对接接口层
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class BaseHttpService {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private static final String GET_DOWNLOAD_LOG_INFO = "=====GET download url：{},result：{}";

    private static final String GET_DOWNLOAD_LOG_ERROR = "=====GET download url：{},result：{},error message：{}";

    private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";


    protected HttpResponse downLoad(String url, String body) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        // 解决中文乱码问题
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setEntity(entity);

        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            log.error("download error:", e);
            throw new MpRuntimeException("download error");
        }

        if (log.isInfoEnabled()) {
            log.info(GET_DOWNLOAD_LOG_INFO, url);
        }
        return response;
    }

    protected <T extends BaseModel> T sendPost(String url, String body, Class<T> valueType) {
        String result = "";
        try {
            result = HttpUtils.sendPost(url, body);
        } catch (IOException e) {
            log.error("send post error:", e);
            throw new MpRuntimeException("send post error");
        }

        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }

        T model = JsonCommon.snakeParse(result, valueType);
        return model;
    }
}
