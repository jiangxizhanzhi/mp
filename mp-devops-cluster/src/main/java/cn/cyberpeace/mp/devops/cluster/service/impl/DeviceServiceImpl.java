package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备
 * @author wanggang
 */
@Service
public class DeviceServiceImpl extends BaseOpenStackServiceImpl implements DeviceService {

    public DeviceServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public Device findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/device/" + id, Device.class);
    }

    @Override
    public DeviceCloudPlatformModel findByIds(List<Integer> ids) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/device/byid?ids=" + Joiner.on(",").join(ids), DeviceCloudPlatformModel.class);
    }

    @Override
    public Boolean deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/batch_delete",JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
        return model.isSuccess();
    }
}
