package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface MicrosoftAzureLoginService {

    /**
     * 微软云账号信息管理列表
     */
    ListObjServiceResult getMicrosoftAzureUserList(JSONObject requestData);

    /**
     * 获取当前云账号信息
     */
    MicrosoftAzureUserModel getLocalAzurer(JSONObject requestData);

    /**
     * 增加微软云用户
     */
    Boolean addAzurer(MicrosoftAzureUserModel requestData);

    /**
     * 删除微软云用户
     */
    Boolean deleteAzurer(String pk);

    /**
     * 修改微软云用户
     */
    Boolean updateLocalAzurer(MicrosoftAzureUserModel requestData);
}
