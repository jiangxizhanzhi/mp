package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 基础对接接口层
 */
@Slf4j
@Service
public class BaseServiceImpl {

    protected DevopsConfig devopsConfig;

    protected ObjectMapper objectMapper;

    @Autowired
    public BaseServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        this.devopsConfig = devopsConfig;
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
            result = HttpUtils.getInstance().sendGet(url);
            log.info("=====GET调用url：{},对应返回结果信息：{}",url,result);
        } catch (IOException e) {
            log.info("=====GET调用url：{},对应返回结果信息：{},报错信息：{}",url,result,e.getMessage());
        }
        return result;
    }

    /**
     * 通过Get方式查询信息
     * @param url
     * @return
     */
    protected String sendGet(String url,Map<String,String> headers){
        String result = "";
        try {
            result = HttpUtils.getInstance().sendGet(url,headers);
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
     * 通过Get方式查询信息
     * @param url
     * @param headers
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendGet(String url, Map<String,String> headers, Class<T> valueType){
        T model = null;
        String result = "";
        try {
            result = this.sendGet(url,headers);
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
            result = HttpUtils.getInstance().sendPost(url,body);
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

    /**
     * 通过Delete方式删除数据
     * @param url
     * @param body
     * @return
     */
    protected String sendDelete(String url,String body){
        String result = "";
        try {
            result = HttpUtils.getInstance().sendDelete(url,body);
            log.info("=====DELETE调用url：{},参数：{},对应返回结果信息：{}",url,body,result);
        } catch (IOException e) {
            log.info("=====DELETE调用url：{},参数：{},对应返回结果信息：{},报错信息：{}",url,body,result,e.getMessage());
        }
        return result;
    }

}
