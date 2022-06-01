package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础对接接口层
 * @author wanggang
 */
@Slf4j
@Service
public class BaseService {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private static final String GET_DOWNLOAD_LOG_INFO = "=====GET download url：{},result：{}";

    private static final String GET_DOWNLOAD_LOG_ERROR = "=====GET download url：{},result：{},error message：{}";

    private static final String GET_LOG_INFO = "=====GET url：{},result：{}";

    private static final String GET_LOG_ERROR = "=====GET url：{},result：{},error message：{}";

    private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";

    private static final String POST_LOG_ERROR = "=====POST url：{},params：{},>>>>>>result：{},error message：{}";

    private static final String PUT_LOG_INFO = "=====PUT url：{},params：{},>>>>>>>result：{}";

    private static final String PUT_LOG_ERROR = "=====PUT url：{},params：{},>>>>>>>result：{},error message：{}";

    private static final String DELETE_LOG_INFO = "=====DELETE url：{},params：{},>>>>>>>result：{}";

    private static final String DELETE_LOG_ERROR = "=====DELETE url：{},params：{},>>>>>>>result：{},error message：{}";


    /**
     * 通过Get方式查询信息
     * @param url
     * @return
     */
    protected HttpResponse downLoad(String url){
        HttpResponse result = null;
        try {
            result = HttpUtils.sendGetForDownLoad(url);
            if (log.isInfoEnabled()) {
                log.info(GET_DOWNLOAD_LOG_INFO, url, result);
            }
        }catch (ConnectException e){
            log.error(GET_DOWNLOAD_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(GET_DOWNLOAD_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
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
            result = HttpUtils.sendGet(url,headers);
            if (log.isInfoEnabled()) {
                log.info(GET_LOG_INFO, url, result);
            }
        }catch (ConnectException e){
            log.error(GET_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(GET_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
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
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result =  HttpUtils.sendGet(url,headers);
            if (log.isInfoEnabled()) {
                log.info(GET_LOG_INFO, url, result);
            }
            model = JsonUtils.toSnakeObject(result,valueType);
        }catch (ConnectException e){
            log.error(GET_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        }catch (IOException e) {
            log.error(GET_LOG_ERROR,url,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
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
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result = HttpUtils.sendPost(url,body,headers);
            if (log.isInfoEnabled()) {
                log.info(POST_LOG_INFO, url, body, result);
            }
        }catch (ConnectException e){
            log.error(POST_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(POST_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
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
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result = HttpUtils.sendPost(url,body,headers);
            if (log.isInfoEnabled()) {
                log.info(POST_LOG_INFO, url, body, result);
            }
            model = JsonUtils.toSnakeObject(result,valueType);
        }catch (ConnectException e){
            log.error(POST_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(POST_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
        }
        return model;
    }

    /**
     * 通过PUT方式更新信息
     * @param url
     * @param body
     * @return
     */
    protected String sendPut(String url,String body){
        String result = "";
        try {
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result = HttpUtils.sendPut(url,body,headers);
            if (log.isInfoEnabled()) {
                log.info(PUT_LOG_INFO, url, body, result);
            }
        }catch (ConnectException e){
            log.error(PUT_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(PUT_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
        }
        return result;
    }

    /**
     * 通过PUT方式更新信息
     * @param url
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendPut(String url,String body,Class<T> valueType){
        T model = null;
        String result = "";
        try {
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result =  HttpUtils.sendPut(url,body,headers);
            if (log.isInfoEnabled()) {
                log.info(PUT_LOG_INFO, url, body, result);
            }
            model = JsonUtils.toSnakeObject(result,valueType);
        }catch (ConnectException e){
            log.error(PUT_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(PUT_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
        }
        return model;
    }

    /**
     * 通过Delete方式删除数据
     * @param url
     * @param body
     * @return
     */
    private String sendDelete(String url,String body){
        String result = "";
        try {
            Map<String,String> headers = wrapperHeaders(new HashMap<>());
            result =  HttpUtils.sendDelete(url,body,headers);
            if (log.isInfoEnabled()) {
                log.info(DELETE_LOG_INFO, url, body, result);
            }
        }catch (ConnectException e){
            log.error(DELETE_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(BaseErrorCode.GATEWAY_TIME_OUT);
        } catch (IOException e) {
            log.error(DELETE_LOG_ERROR,url,body,result,e.getMessage(),e);
            throw new MpRuntimeException(e.getMessage()).e(e);
        }
        return result;
    }

    /**
     * 删除
     * @param url
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendDelete(String url,String body,Class<T> valueType){
        String result = this.sendDelete(url,body);
        T model = JsonUtils.toSnakeObject(result,valueType);
        if (log.isInfoEnabled()) {
            log.info(DELETE_LOG_INFO, url, body, result);
        }
        return model;
    }

    /**
     * 表单新增
     * @param url
     * @param httpEntity
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    protected <T extends BaseModel> T sendFormDataPost(String url, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        Map<String,String> headers = wrapperHeaders(new HashMap<>());
        String result = HttpUtils.doFormDataPost(url,httpEntity,headers);
        T model = JsonUtils.toSnakeObject(result,valueType);
        if (log.isInfoEnabled()) {
            log.info("=====FormDataPost url：{},params：{},result：{}", url, httpEntity, result);
        }
        return model;
    }

    /**
     * 表单更新
     * @param url
     * @param httpEntity
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    protected <T extends BaseModel> T sendFormDataPut(String url, HttpEntity httpEntity ,Class<T> valueType) throws IOException {
        Map<String,String> headers = wrapperHeaders(new HashMap<>());
        String result = HttpUtils.doFormDataPut(url,httpEntity,headers);
        if (log.isInfoEnabled()) {
            log.info("=====FormData url：{},params：{},result：{}", url, httpEntity, result);
        }
        return JsonUtils.toSnakeObject(result,valueType);
    }

    /**
     * 包装请求头信息
     * @param headers
     * @return
     */
    private Map<String,String> wrapperHeaders(Map<String,String> headers){
        if (MapUtils.isEmpty(headers)){
            headers = new HashMap<>();
        }
        headers.put(ACCEPT_LANGUAGE,ServletUtils.getAcceptLanguage());
        return headers;
    }
}
