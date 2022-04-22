package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * wazuh的数据层操作封装dao
 */
@Repository
@Slf4j
public class WazuhRepository {

    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;

    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    /**
     * 查询wazuhToken防止token失效
     * @return
     * @throws IOException
     */
    public String queryWazuhToken() throws IOException {
        var requestUrl = wazuhUrl + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, wazuhUsername, wazuhPassword);
    }


    /**
     * 查询文件监控
     * @param requestUri requestUri
     * @return WazuhFileRoot
     */
    public WazuhFileRoot queryFileEvents(String requestUri){
        var requestUrl = wazuhUrl + requestUri;
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhFileRoot.class);
    }

    /**
     * 查询机器所有的mac地址关系 这里如果遇到性能瓶颈，可做guava缓存，或者缓存查询单个
     * @return
     */
    public WazuhNetifaceRoot queryAllAgentMac() {
        var requestUri = "/experimental/syscollector/ports";
        var requestUrl = wazuhUrl + requestUri;
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhNetifaceRoot.class);
    }


}
