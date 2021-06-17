package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpsUtils;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import cn.cyberpeace.mp.datasandtable.model.WazuhRamRoot;
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
        return HttpsUtils.sendGetWithAuth(requestUrl, wazuhUsername, wazuhPassword);
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
            res = HttpsUtils.sendGetWithAuthToken(requestUrl, DataSandtableConstant.wazuhToken.get("wazuhToken"));
        } catch (Exception e) {
            log.error("第一次查询ram信息失败:");
            log.info("重试查询");
            DataSandtableConstant.wazuhToken.invalidate("wazuhToken");
            try {
                res = HttpsUtils.sendGetWithAuthToken(requestUrl, DataSandtableConstant.wazuhToken.get("wazuhToken"));
            }catch (Exception ex){
                log.error("重试查询ram信息失败:" + ex);
                throw new MpRuntimeException("查询ram信息失败");
            }
        }
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhRamRoot.class);
    }

}
