package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.base.util.HttpsUtils;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * wazuh的数据层操作封装dao
 */
@Repository
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
}
