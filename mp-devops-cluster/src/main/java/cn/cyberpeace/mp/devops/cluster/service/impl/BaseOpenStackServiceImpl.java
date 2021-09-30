package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        return super.sendGet(devopsConfig.getCpUrl().concat(api),valueType);
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
        return super.sendGet(devopsConfig.getCpUrl().concat(api),valueType);
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
        return super.sendPost(devopsConfig.getCpUrl().concat(api),body,valueType);
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @return
     */
    protected String sendPost(String api,String body){
        return super.sendPost(devopsConfig.getCpUrl().concat(api),body);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendPut(String api,String body,Class<T> valueType){
        return super.sendPut(devopsConfig.getCpUrl().concat(api),body,valueType);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @param body
     * @return
     */
    @Override
    protected String sendPut(String api,String body){
        return super.sendPut(devopsConfig.getCpUrl().concat(api),body);
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api){
        return super.downLoad(devopsConfig.getCpUrl().concat(api));
    }

    /**
     * 通过Delete方式删除数据
     * @param api
     * @param body
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendDelete(String api, String body,Class<T> valueType){
        return super.sendDelete(devopsConfig.getCpUrl().concat(api),body, valueType);
    }

    /**
     * 上传文件
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPost(String api, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        return super.sendFormDataPost(devopsConfig.getCpUrl().concat(api),httpEntity,valueType);
    }

    /**
     * 上传文件
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPut(String api, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        return super.sendFormDataPut(devopsConfig.getCpUrl().concat(api),httpEntity,valueType);
    }
}
