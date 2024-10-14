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
import org.springframework.util.StringUtils;

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
        if (requestData == null) {
            return new ServiceResult<>(null, BaseErrorCode.ERROR, "请求数据为空").asResult();
        }

        // 验证私有云地址格式
        String connectIp = requestData.getConnectIp();
        if (!StringUtils.isEmpty(connectIp) && !CheckUtil.checkPrivateCloudAddress(connectIp)) {
            return new ServiceResult<>(null, BaseErrorCode.ERROR, "私有云地址格式错误").asResult();
        }

        // 判断是否为新增账号（没有主键表示新增）
        if (StringUtils.isEmpty(requestData.getPk())) {
            // 调用新增逻辑
            Boolean addAzurer = microsoftAzureLoginMapper.addAzurer(requestData);
            if (addAzurer) {
                return new ServiceResult<>(null, BaseErrorCode.SUCCESS, "新增账号信息成功").asResult();
            } else {
                return new ServiceResult<>(null, BaseErrorCode.ERROR, "新增账号信息失败").asResult();
            }
        }

        // 检查是否有需要更新的字段
        if (isUpdateNeeded(requestData)) {
            Boolean updateAzurer = microsoftAzureLoginMapper.updateLocalAzurer(requestData.getPk().toString(), requestData);
            if (updateAzurer) {
                return new ServiceResult<>(null, BaseErrorCode.SUCCESS, "修改账号信息成功").asResult();
            } else {
                return new ServiceResult<>(null, BaseErrorCode.ERROR, "修改账号信息失败").asResult();
            }
        }

        return new ServiceResult<>(null, BaseErrorCode.SUCCESS, "无修改信息，操作成功").asResult();
    }

    // 检查是否有字段需要更新
    private boolean isUpdateNeeded(MicrosoftAzureUserModel requestData) {
        return !StringUtils.isEmpty(requestData.getConnectIp()) ||
                !StringUtils.isEmpty(requestData.getAzureTenantId()) ||
                !StringUtils.isEmpty(requestData.getAzureSubscriptionId()) ||
                !StringUtils.isEmpty(requestData.getAzureClientId()) ||
                !StringUtils.isEmpty(requestData.getAzureClientSecret());
    }
}
