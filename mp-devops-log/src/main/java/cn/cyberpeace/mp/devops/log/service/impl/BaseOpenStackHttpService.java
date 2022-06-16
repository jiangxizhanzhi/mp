package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.model.BaseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 基础对接云平台接口层
 *
 * @author wanggang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseOpenStackHttpService extends BaseHttpService {

    @Value("${mp.devops.cp-url}")
    private String cpUrl;

    /**
     * 通过Get方式查询信息
     *
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendGet(String api, Class<T> valueType) {
        return super.sendGet(cpUrl.concat(api), valueType);
    }

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
        return super.sendPost(cpUrl.concat(api), body, valueType);
    }

    /**
     * 通过Post方式查询信息
     *
     * @param api
     * @param body
     * @return
     */
    @Override
    protected String sendPost(String api, String body) {
        return super.sendPost(cpUrl.concat(api), body);
    }

    /**
     * 通过PUT方式查询信息
     *
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendPut(String api, String body, Class<T> valueType) {
        return super.sendPut(cpUrl.concat(api), body, valueType);
    }

    /**
     * 通过PUT方式查询信息
     *
     * @param api
     * @param body
     * @return
     */
    @Override
    protected String sendPut(String api, String body) {
        return super.sendPut(cpUrl.concat(api), body);
    }

    /**
     * 通过PUT方式查询信息
     *
     * @param api
     * @return
     */
    @Override
    protected HttpResponse downLoad(String api) {
        return super.downLoad(cpUrl.concat(api));
    }

    /**
     * 通过Delete方式删除数据
     *
     * @param api
     * @param body
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendDelete(String api, String body, Class<T> valueType) {
        return super.sendDelete(cpUrl.concat(api), body, valueType);
    }

    /**
     * 上传文件
     *
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPost(String api, HttpEntity httpEntity, Class<T> valueType) throws IOException {
        return super.sendFormDataPost(cpUrl.concat(api), httpEntity, valueType);
    }

    /**
     * 上传文件
     *
     * @param api
     * @param httpEntity
     * @return
     */
    @Override
    protected <T extends BaseModel> T sendFormDataPut(String api, HttpEntity httpEntity, Class<T> valueType) throws IOException {
        return super.sendFormDataPut(cpUrl.concat(api), httpEntity, valueType);
    }
}
