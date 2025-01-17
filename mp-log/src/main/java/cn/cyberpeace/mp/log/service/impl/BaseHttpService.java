 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseHttpService.java.
 // File size 1874.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.model.BaseModel;
import cn.cyberpeace.mp.log.utils.JsonUtils;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

 /**
  * 基础对接接口层
  *
  * @author wanggang@date 2025/01/17
  */
 @Slf4j
@Service
public class BaseHttpService {

     /**
      * 接受语言
      */
     private static final String ACCEPT_LANGUAGE = "Accept-Language";

     /**
      * 获取下载日志信息
      */
     private static final String GET_DOWNLOAD_LOG_INFO = "=====GET download url：{},result：{}";

     /**
      * 获取下载日志错误
      */
     private static final String GET_DOWNLOAD_LOG_ERROR = "=====GET download url：{},result：{},error message：{}";

     /**
      * 发布日志信息
      */
     private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";

     /**
      * 转载
      *
      * @param url  url
      * @param body 身体
      * @return {@link HttpResponse }
      */
     protected HttpResponse downLoad(String url,String body){
        HttpResponse result = null;
        result = HttpRequest.post(url).body(body).execute();
        if (log.isInfoEnabled()) {
            log.info(GET_DOWNLOAD_LOG_INFO, url);
        }
        return result;
    }

     /**
      * 通过Post方式查询信息
      *
      * @param url
      * @param body
      * @param valueType
      * @return
      */
     protected <T extends BaseModel> T sendPost(String url, String body, Class<T> valueType) {
        T model = null;
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        result = HttpRequest.post(url).body(body).header(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage()).execute().body();
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        model = JsonUtils.toSnakeObject(result, valueType);
        return model;
    }
}
