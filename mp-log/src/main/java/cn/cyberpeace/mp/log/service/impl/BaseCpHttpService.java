// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCpHttpService.java.
 // File size 1212.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.model.BaseModel;
import cn.hutool.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 /**
  * 基础对接云平台接口层
  *
  * @author wanggang@date 2025/01/17
  */
 @Slf4j
@Service
@RequiredArgsConstructor
public class BaseCpHttpService extends BaseHttpService {

     /**
      * 日志配置
      */
     @Autowired
    private LogConfig logConfig;

     /**
      * 通过Post方式查询信息
      *
      * @param api
      * @param body
      * @param valueType
      * @return
      */
     @Override
    protected <T extends BaseModel> T sendPost(String api, String body, Class<T> valueType) {
        return super.sendPost(logConfig.getCpUrl().concat(api), body, valueType);
    }

     /**
      * 通过PUT方式查询信息
      *
      * @param api
      * @param body 身体
      * @return
      */
     @Override
    protected HttpResponse downLoad(String api, String body) {
        return super.downLoad(logConfig.getCpUrl().concat(api),body);
    }
}
