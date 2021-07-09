package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 基础对接云平台接口层
 */
@Slf4j
@Service
public class BaseOpenStackServiceImpl extends BaseServiceImpl {

    public BaseOpenStackServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 通过Get方式查询信息
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendGet(String api, Class<T> valueType){
        return super.sendGet(devopsConfig.getCp_url().concat(api),valueType);
    }

    /**
     * 通过Get方式查询信息
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendGet(String api,Map<String,String> headers, Class<T> valueType){
        return super.sendGet(devopsConfig.getCp_url().concat(api),valueType);
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendPost(String api,String body,Class<T> valueType){
        return super.sendPost(devopsConfig.getCp_url().concat(api),body,valueType);
    }
}
