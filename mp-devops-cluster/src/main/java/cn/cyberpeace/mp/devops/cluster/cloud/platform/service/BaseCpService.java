package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 基础对接云平台接口层
 * @author wanggang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseCpService {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private static final String GET_LOG_INFO = "=====GET url：{},result：{}";

    private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";

    private static final String PUT_LOG_INFO = "=====PUT url：{},params：{},>>>>>>>result：{}";

    private static final String DELETE_LOG_INFO = "=====DELETE url：{},params：{},>>>>>>>result：{}";

    private final DevopsConfig devopsConfig;

    /**
     * 通过Get方式查询信息
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendGet(String api, Class<T> valueType){
        String url = this.buildUrl(api);
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(GET_LOG_INFO, url, result);
        }
        result = HttpRequest.get(url).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (log.isInfoEnabled()) {
            log.info(GET_LOG_INFO, url, result);
        }
        return this.buildSnakeObject(result, valueType);
    }

    /**
     * 通过Get方式查询信息
     * @param api
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendGet(String api,int timeout, Class<T> valueType){
        String url = this.buildUrl(api);
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(GET_LOG_INFO, url, result);
        }
        result = HttpRequest.get(url).addHeaders(wrapperHeaders(new HashMap<>())).timeout(timeout).execute().body();
        if (log.isInfoEnabled()) {
            log.info(GET_LOG_INFO, url, result);
        }
        return this.buildSnakeObject(result, valueType);
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendPost(String api,String body,Class<T> valueType){
        String url = this.buildUrl(api);
        T model = null;
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        result = HttpRequest.post(url).body(body).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        model = this.buildSnakeObject(result, valueType);
        return model;
    }

    /**
     * 通过Post方式查询信息
     * @param api
     * @param body
     * @return
     */
    protected String sendPost(String api,String body){
        String url = this.buildUrl(api);
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        result = HttpRequest.post(url).body(body).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        return result;
    }

    /**
     * 通过PUT方式查询信息
     * @param api
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendPut(String api,String body,Class<T> valueType){
        String url = this.buildUrl(api);
        T model = null;
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(PUT_LOG_INFO, url, body, result);
        }
        result = HttpRequest.put(url).body(body).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (log.isInfoEnabled()) {
            log.info(PUT_LOG_INFO, url, body, result);
        }
        model = this.buildSnakeObject(result, valueType);
        return model;
    }

    /**
     * 通过Delete方式删除数据
     * @param api
     * @param body
     * @return
     */
    protected <T extends BaseModel> T sendDelete(String api, String body,Class<T> valueType){
        String url = this.buildUrl(api);

        String result = "";
        if (log.isInfoEnabled()) {
            log.info(DELETE_LOG_INFO, url, body, result);
        }

        result = HttpRequest.delete(url).body(body).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();

        T model = this.buildSnakeObject(result, valueType);
        if (log.isInfoEnabled()) {
            log.info(DELETE_LOG_INFO, url, body, result);
        }
        return model;
    }

    /**
     * 上传文件
     * @param api
     * @param formMap
     * @return
     */
    protected <T extends BaseModel> T sendFormDataPost(String api, Map<String, Object> formMap , Class<T> valueType) throws IOException {
        String url = this.buildUrl(api);
        String result = HttpUtil.createPost(url).form(formMap).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        T model = this.buildSnakeObject(result, valueType);
        if (log.isInfoEnabled()) {
            log.info("=====FormDataPost url：{},params：{},result：{}", url, formMap, result);
        }
        return model;
    }

    /**
     * 上传文件
     * @param api
     * @param fileList
     * @return
     */
    protected <T extends BaseModel> T sendFormDataPost(String api, List<File> fileList , Class<T> valueType) throws IOException {
        String url = this.buildUrl(api);
        String result = HttpUtil.createPost(url).addHeaders(wrapperHeaders(new HashMap<>())).form("file",fileList.toArray(File[]::new)).execute().body();
        T model = this.buildSnakeObject(result, valueType);
        if (log.isInfoEnabled()) {
            log.info("=====FormDataPost url：{},result：{}", url, result);
        }
        return model;
    }

    /**
     * 上传文件
     * @param api
     * @param formMap
     * @return
     */
    protected <T extends BaseModel> T sendFormDataPut(String api, Map<String, Object> formMap ,Class<T> valueType) throws IOException {
        String url = this.buildUrl(api);
        String result = HttpRequest.put(url).form(formMap).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (log.isInfoEnabled()) {
            log.info("=====FormData url：{},params：{},result：{}", url, formMap, result);
        }
        return this.buildSnakeObject(result, valueType);
    }

    /**
     * 通用分页接口
     * @param url 请求URL 例如：/abc/page?name=test&type=1
     * @param tClass 数据转换类
     * @param <T>
     * @return
     */
    protected <T> CloudPlatformPageResult<T> page(String url, Class<T> tClass) {
        CloudPlatformCommonPageResult pageResult = this.sendGet(url, CloudPlatformCommonPageResult.class);
        this.checkResult(pageResult);
        CloudPlatformCommonPageResult.CloudPlatformPageData data = pageResult.getData();
        List<T> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()),tClass);
        CloudPlatformPageResult<T> model = new CloudPlatformPageResult<>();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     * 检查请求云平台接口
     * @param t
     * @param <T>
     */
    protected <T extends BaseCloudPlatformResult> void checkResult(T t) {
        if (Objects.isNull(t) || (!BaseCloudPlatformConstant.CODE.equals(t.getCode()) && !t.isSuccess())) {
            String message = CloudMessageConvertUtil.getMessage(t.getMessage());
            if (StringUtils.isNotEmpty(message)) {
                throw new MpRuntimeException(DevopsClusterErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format(message));
            } else {
                throw new MpRuntimeException(DevopsClusterErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format(t.getMessage()));
            }
        }
    }

    @Data
    @NoArgsConstructor
    private static class CloudPlatformCommonPageResult extends BaseCloudPlatformResult {

        private CloudPlatformPageData data;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CloudPlatformPageData {

            private List<?> list = new ArrayList<>();

            private Integer total = 0;
        }
    }

    /**
     * 包装请求头信息
     *
     * @param headers
     * @return
     */
    private Map<String, String> wrapperHeaders(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        return headers;
    }

    /**
     * 获取请求链接URL
     * @param api 接口URL
     * @return
     */
    private String buildUrl(String api){
        return devopsConfig.getCpUrl().concat(api);
    }

    /**
     * 构建驼峰对象
     * @param result
     * @param tClass
     * @param <T>
     * @return
     */
    private <T extends BaseModel> T buildSnakeObject(String result, Class<T> tClass){
        T toSnakeObject;
        try {
            toSnakeObject = JsonUtils.toSnakeObject(result, tClass);
        } catch (Exception e) {
            log.error("cp service result toSnakeObject error:", e);
            throw new MpRuntimeException(DevopsClusterErrorCode.CP_SERVICE_ERROR);
        }
        return toSnakeObject;
    }
}
