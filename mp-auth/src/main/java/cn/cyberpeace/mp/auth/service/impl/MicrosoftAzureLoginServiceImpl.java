package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MicrosoftAzureLoginServiceImpl implements MicrosoftAzureLoginService  {

    @Autowired
    private MicrosoftAzureLoginMapper microsoftAzureLoginMapper;

    /**
     * 微软云账号信息管理列表
     */
    @Override
    public ListObjServiceResult getMicrosoftAzureUserList(JSONObject requestData) {
        Integer pageNum = requestData != null && requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData != null && requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MicrosoftAzureUserModel> list = microsoftAzureLoginMapper.getMicrosoftAzureUserList(requestData);
        return new ListObjServiceResult<>(new ListObjResult<>(list, list.size()));
    }

    public MicrosoftAzureUserModel getLocalAzurer(JSONObject requestData) {
        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginMapper.getLocalAzurer(requestData);
        return localAzurer;
    }

    @Override
    @Transactional
    public Boolean addAzurer(JSONObject requestData) {
        if(requestData != null){
            Boolean addAzurer = microsoftAzureLoginMapper.addAzurer(requestData);
            if(addAzurer){
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteAzurer(String pk) {
        if(pk != null && !pk.equals("")){
            Boolean deleteAzurer = microsoftAzureLoginMapper.deleteAzurer(pk);
            if(deleteAzurer){
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean updateLocalAzurer(JSONObject requestData) {
        if (requestData != null && requestData.get("pk") != null && requestData.get("pk") instanceof String
                && !requestData.get("pk").toString().equals("")) {
            Boolean updateLocalAzurer = microsoftAzureLoginMapper.updateLocalAzurer(requestData.get("pk").toString(),requestData);
            if(updateLocalAzurer){
                return true;
            }
        }
        return false;
    }

}
