package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.util.BaseResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface MicrosoftAzureLoginService {

    /**
     * 微软云账号信息管理列表
     */
    BaseResponse getMicrosoftAzureUserList(JSONObject requestData);

}
