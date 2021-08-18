package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.service.InstallerService;
import com.alibaba.fastjson.JSONObject;
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
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists", JSONObject.toJSONString(installer),Installer.class);
    }

    @Override
    public String update(String uuid, Installer installer) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + uuid,JSONObject.toJSONString(installer));
    }

    @Override
    public Installer findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + id, Installer.class);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists",JSONObject.toJSONString(jsonObject),BaseCloudPlatformModel.class);
    }
}
