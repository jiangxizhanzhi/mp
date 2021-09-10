package cn.cyberpeace.mp.base.util.http;

import org.apache.http.client.config.RequestConfig;

import java.util.Collections;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/8
 */
public class MpHttpArgs {

    private MpHttpHeaders headers;

    private ParamsContainer bodyContent;

    private ParamsContainer urlParams;

    private RequestConfig requestConfig;

    public static MpHttpArgs builder() {
        return new MpHttpArgs();
    }

    private MpHttpArgs() {

    }

    public ParamsContainer getBodyContent() {
        return bodyContent;
    }

    public ParamsContainer getUrlParams() {
        return urlParams;
    }

    public Map<String, String> getHeaders() {
        return this.headers == null ? Collections.EMPTY_MAP : this.headers.getValues();
    }


    public MpHttpArgs setBodyContent(ParamsContainer bodyContent) {
        this.bodyContent = bodyContent;
        return this;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public MpHttpArgs setHeaders(MpHttpHeaders headers) {
        this.headers = headers;
        return this;
    }


    public MpHttpArgs setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    public MpHttpArgs setUrlParams(ParamsContainer urlParams) {
        this.urlParams = urlParams;
        return this;
    }

    public MpHttpArgs build() {
        return this;
    }

}
