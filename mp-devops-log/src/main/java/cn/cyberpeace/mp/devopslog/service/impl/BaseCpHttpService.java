package cn.cyberpeace.mp.devopslog.service.impl;

import cn.cyberpeace.mp.devopslog.config.DevopsLogConfig;
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

    @Autowired
    private DevopsLogConfig devopsLogConfig;

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
        return super.sendPost(devopsLogConfig.getCpUrl().concat(api), body, valueType);
    }

    /**
     * 通过PUT方式查询信息
     *
     * @param api
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api, String body) {
        return super.downLoad(devopsLogConfig.getCpUrl().concat(api),body);
    }
}
