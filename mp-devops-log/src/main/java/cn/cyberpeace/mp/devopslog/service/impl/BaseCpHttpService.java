package cn.cyberpeace.mp.devopslog.service.impl;

import cn.cyberpeace.mp.common.config.CommonAppConfig;
import cn.cyberpeace.mp.devopslog.model.BaseModel;
import cn.hutool.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 基础对接云平台接口层
 *
 * @author wanggang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseCpHttpService extends BaseHttpService {

    /**
     * 通过 commonAppConfig 获取云平台地址
     */
    @Autowired
    private CommonAppConfig commonAppConfig;

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
        return super.sendPost(commonAppConfig.getCpUrl().concat(api), body, valueType);
    }

    /**
     * 通过PUT方式查询信息
     *
     * @param api
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api, String body) {
        return super.downLoad(commonAppConfig.getCpUrl().concat(api),body);
    }
}
