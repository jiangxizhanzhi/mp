package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.common.util.Ec2Signer;
import cn.cyberpeace.mp.base.config.XdAppConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class XdTwoHttpClient {


    private static final String region = "cn-south-1";
    private static final String service = "DSP-Client";
    private static final String strSignedHeader = "path;";
    // 认证范围,为了配合scp端，可以指定，不指定的话自动生成
    private static final String credentialScope = region + "/" + service + "/" + "dmp_request";

    private HttpClient httpClient;

    @Autowired
    private XdAppConfig xdAppConfig;

    public void init() {
        httpClient = HttpClient.builder()
                .configServer(xdAppConfig.getXdTwoServer())
                .configTimeout(60000)
                .configMaxConnTotal(20)
                .configMaxConnPerRoute(20)
                .build();
        httpClient.setLogRequest(true);
    }

    public HttpResult xdGet(String url, HttpParam param) {
        return httpClient.get(url, param, this.getHeaders(url, "GET", null), null);
    }

    public HttpResult xdPost(String url, HttpParam param, HttpData data) {
        return httpClient.post(url, data, param, this.getHeaders(url, "POST", data), null);
    }

    private Map<String, String> getHeaders(String url, String method, HttpData data) {
        Ec2Signer ec2Signer;
        if (data == null) {
            ec2Signer = new Ec2Signer.Builder(xdAppConfig.getAccessKey(), xdAppConfig.getSecretKey())
                    .regionName(region)
                    .serviceName(service)
                    .httpMethodName(method)
                    .canonicalURI(url)
                    .strSignedHeader(strSignedHeader)
                    .credentialScope(credentialScope)
                    .debug()  //开启打印
                    .build();
        } else {
            JSONObject jsonObject = new JSONObject(data.toMultipart());
            String payload = jsonObject.toString();
            ec2Signer = new Ec2Signer.Builder(xdAppConfig.getAccessKey(), xdAppConfig.getSecretKey())
                    .regionName(region)
                    .serviceName(service)
                    .httpMethodName(method)
                    .canonicalURI(url)
                    .payload(payload)
                    .strSignedHeader(strSignedHeader)
                    .credentialScope(credentialScope)
                    .debug()  //开启打印
                    .build();
        }
        Map<String, String> headers = ec2Signer.getHeaders();
        // 国际化header的处理
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            headers.put("Accept-Language", locale.getLanguage());
        }
        // 调用链id
        String traceId = SysContextHolder.getCallChainId();
        headers.put("X-Request-Id", traceId);

        headers.put("Cookie", "aCMPAuthToken=1d3747c7af6f40f29e70788e2d1fbe2a");
        return headers;
    }

}
