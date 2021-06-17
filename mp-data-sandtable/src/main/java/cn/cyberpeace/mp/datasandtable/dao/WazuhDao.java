package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import cn.cyberpeace.mp.datasandtable.model.WazuhRamRoot;
import cn.cyberpeace.mp.datasandtable.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * wazuh的数据层操作封装dao
 */
@Repository
@Slf4j
public class WazuhDao {

    @Value("${mp.datasandtable.wazuh.base-url}")
    private String wazuhUrl;

    @Value("${mp.datasandtable.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.datasandtable.wazuh.password}")
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
     * 查询ram信息
     * @param agentId
     * @return
     * @throws IOException
     */
    public WazuhRamRoot queryRamByAgentId(String agentId) {
        var requestUrl = wazuhUrl + "/syscollector/" + agentId + "/hardware";
        String res = null;
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        } catch (Exception e) {
            log.error("查询ram信息失败:" + e);
            throw new MpRuntimeException("查询ram信息失败");
        }
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhRamRoot.class);
    }

}
