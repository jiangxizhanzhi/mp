package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;


@Repository
@Slf4j
public class WazuhRepository {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    
    public String queryWazuhToken() throws IOException {
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, situationAwareConfig.getWazuhUsername(), situationAwareConfig.getWazuhPassword());
    }


    
    public WazuhFileRoot queryFileEvents(String requestUri){
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + requestUri;
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhFileRoot.class);
    }

    
    public WazuhNetifaceRoot queryAllAgentMac() {
        var requestUri = "/experimental/syscollector/ports";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + requestUri;
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhNetifaceRoot.class);
    }


}
