package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.record.FeatHdrRecord;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProtocolProxyClient {

    @Resource
    private BaseAppConfig baseAppConfig;

    private volatile HttpClient httpClient;


    private HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = HttpClient.builder()
                            .configServer(baseAppConfig.getProxyClient())
                            .build();
                    httpClient.setLogRequest(true);
                }
            }
        }
        return httpClient;
    }


    public String getProxyLink(ProtocolProxyHttpData protocolProxyHttpData) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String result = getHttpClient().post("/api/connect", protocolProxyHttpData, null, headers, 10000)
                .getResult();
        return JSONObject.parseObject(result).getString("url").replace("http://localhost:3000", baseAppConfig.getProxyClient());
    }

}
