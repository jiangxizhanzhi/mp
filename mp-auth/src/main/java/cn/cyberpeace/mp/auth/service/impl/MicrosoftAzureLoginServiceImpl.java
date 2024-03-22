package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.auth.util.ResponseUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    public BaseResponse getMicrosoftAzureUserList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        PageHelper.startPage(pageNum,pageSize);
        List<MicrosoftAzureUserModel> list = microsoftAzureLoginMapper.getMicrosoftAzureUserList(requestData);
        PageInfo<MicrosoftAzureUserModel> pageInfo = new PageInfo<>(list);
        return ResponseUtil.success(pageInfo);
    }

}
