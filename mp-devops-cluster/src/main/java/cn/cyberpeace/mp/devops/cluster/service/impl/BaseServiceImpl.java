package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 基础对接接口层
 */
@Slf4j
@Service
public class BaseServiceImpl {

    protected OpenStackConfig openStackConfig;

    protected ObjectMapper objectMapper;

    @Autowired
    public BaseServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        this.openStackConfig = openStackConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * 通过Get方式查询信息
     * @param url
     * @return
     */
    protected String sendGet(String url){
        String result = "";
        try {
            result = HttpUtils.sendGet(url);
            log.info("=====GET调用url：{},对应返回结果信息：{}",url,result);
        } catch (IOException e) {
            log.info("=====GET调用url：{},对应返回结果信息：{},报错信息：{}",url,result,e.getMessage());
        }
        return result;
    }

    /**
     * 通过Get方式查询信息
     * @param url
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendGet(String url, Class<T> valueType){
        T model = null;
        String result = "";
        try {
            result = this.sendGet(url);
            model = objectMapper.readValue(result,valueType);
        } catch (IOException e) {
            log.info("=====GET调用url：{},对应返回结果信息：{},报错信息：{}",url,result,e.getMessage());
        }
        return model;
    }

    /**
     * 通过Post方式查询信息
     * @param url
     * @param body
     * @return
     */
    protected String sendPost(String url,String body){
        String result = "";
        try {
            result = HttpUtils.sendPost(url,body);
            log.info("=====POST调用url：{},参数：{},对应返回结果信息：{}",url,body,result);
        } catch (IOException e) {
            log.info("=====POST调用url：{},参数：{},对应返回结果信息：{},报错信息：{}",url,body,result,e.getMessage());
        }
        return result;
    }

    /**
     * 通过Post方式查询信息
     * @param url
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendPost(String url,String body,Class<T> valueType){
        T model = null;
        String result = "";
        try {
            result = this.sendPost(url,body);
            model = objectMapper.readValue(result,valueType);
        } catch (IOException e) {
            log.info("=====POST调用url：{},参数：{},对应返回结果信息：{},报错信息：{}",url,body,result,e.getMessage());
        }
        return model;
    }
}
