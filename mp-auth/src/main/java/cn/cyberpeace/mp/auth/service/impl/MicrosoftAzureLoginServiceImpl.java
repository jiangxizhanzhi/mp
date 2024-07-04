package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.util.CheckUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        requestData.put("pageSize",pageSize);
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MicrosoftAzureUserModel> list = microsoftAzureLoginMapper.getMicrosoftAzureUserList(requestData);
        return new ListObjServiceResult<>(new ListObjResult<>(list, list.size()));
    }

    public MicrosoftAzureUserModel getLocalAzurer(JSONObject requestData) {
        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginMapper.getLocalAzurer(requestData);
        return localAzurer;
    }

    @Override
    public Boolean deleteAzurer(String pk) {
        if(pk == null || pk.equals("")){
            return false;
        }
        Boolean deleteAzurer = microsoftAzureLoginMapper.deleteAzurer(pk);
        return deleteAzurer;
    }

    @Override
    public ServiceResult updateLocalAzurer(MicrosoftAzureUserModel requestData) {
        if(requestData.getConnectIp() != null && !requestData.getConnectIp().equals("")){
            Boolean aBoolean = CheckUtil.checkPrivateCloudAddress(requestData.getConnectIp());
            if(!aBoolean){
                return new ServiceResult<>(null, BaseErrorCode.ERROR,"私有云地址格式错误");
            }
        }
        if(requestData == null || requestData.getPk() == null || requestData.getPk().equals("")){
            Boolean addAzurer = microsoftAzureLoginMapper.addAzurer(requestData);
            if(addAzurer){
                return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"新增账号信息成功");
            }else{
                return new ServiceResult<>(null, BaseErrorCode.ERROR,"新增账号信息失败");
            }
        }else{
            Boolean updateLocalAzurer = microsoftAzureLoginMapper.updateLocalAzurer(requestData.getPk().toString(),requestData);
            if(updateLocalAzurer){
                return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"修改账号信息成功");
            }else{
                return new ServiceResult<>(null, BaseErrorCode.ERROR,"修改账号信息失败");
            }
        }
    }
}
