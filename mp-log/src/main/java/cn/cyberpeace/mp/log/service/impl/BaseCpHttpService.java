 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCpHttpService.java.
 // File size 1318.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.model.BaseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName BaseCpHttpService
 * @Description 基础对接云平台接口层
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseHttpService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseCpHttpService extends BaseHttpService {

    @Autowired
    private LogConfig logConfig;

    /**
     * 通过Post方式查询信息
     *
     * @param api
     * @param body
     * @param valueType
     * @param <T>
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
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api, String body) {
        return super.downLoad(logConfig.getCpUrl().concat(api),body);
    }
}
