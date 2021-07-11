package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.model.BaseModel;
import cn.cyberpeace.mp.log.utils.JsonUtils;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class BaseHttpService {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private static final String GET_DOWNLOAD_LOG_INFO = "=====GET download url：{},result：{}";

    private static final String GET_DOWNLOAD_LOG_ERROR = "=====GET download url：{},result：{},error message：{}";

    private static final String POST_LOG_INFO = "=====POST url：{},params：{},>>>>>result：{}";

    protected HttpResponse downLoad(String url,String body){
        HttpResponse result = null;
        result = HttpRequest.post(url).body(body).execute();
        if (log.isInfoEnabled()) {
            log.info(GET_DOWNLOAD_LOG_INFO, url);
        }
        return result;
    }

    
    protected <T extends BaseModel> T sendPost(String url, String body, Class<T> valueType) {
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
