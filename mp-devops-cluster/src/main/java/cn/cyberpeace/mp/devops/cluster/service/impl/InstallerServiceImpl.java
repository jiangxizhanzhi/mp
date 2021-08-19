package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.service.InstallerService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallerServiceImpl extends BaseOpenStackServiceImpl implements InstallerService {

    public InstallerServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public InstallerCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists" + getParams, InstallerCloudPlatformModel.class);
    }

    @Override
    public Installer add(Installer installer) {
        try {
            return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists", JsonUtils.toUnderlineJSONString(installer),Installer.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Installer update(String uuid, Installer installer) {
        try {
            return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + uuid,JsonUtils.toUnderlineJSONString(installer),Installer.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Installer findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + id, Installer.class);
    }

    @Override
    public Boolean deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists",JSONObject.toJSONString(jsonObject),BaseCloudPlatformModel.class);
        return model.isSuccess();
    }
}
