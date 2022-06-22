package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.model.BaseModel;
import cn.cyberpeace.mp.devops.log.utils.JsonUtils;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;

/**
 * 基础对接接口层
 * @author wanggang
 */
@Slf4j
@Service
public class BaseHttpService {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private static final String GET_DOWNLOAD_LOG_INFO = "=====GET download url：{},result：{}";

    private static final String GET_DOWNLOAD_LOG_ERROR = "=====GET download url：{},result：{},error message：{}";

    private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";

    protected HttpResponse downLoad(String url,String body){
        HttpResponse result = null;
        try {
            result = HttpUtils.sendPostForDownLoad(url,body);
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
     * 通过Post方式查询信息
     * @param url
     * @param body
     * @param valueType
     * @param <T>
     * @return
     */
    protected <T extends BaseModel> T sendPost(String url,String body,Class<T> valueType) {
        T model = null;
        String result = "";
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        result = HttpRequest.post(url).body(body).header(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage()).execute().body();
        if (log.isInfoEnabled()) {
            log.info(POST_LOG_INFO, url, body, result);
        }
        model = JsonUtils.toSnakeObject(result, valueType);
        return model;
    }
}
