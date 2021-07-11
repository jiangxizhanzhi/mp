package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;


@Service
@Slf4j
public class DeviceService extends BaseCpService {

    private static final String DEVICE = "/device";

    public DeviceService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<Device> page(String params){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/lists" + params, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Device.class);
        CloudPlatformPageResult<Device> model = new CloudPlatformPageResult<>();
        model.setList(list);
        model.setTotal(pageResult.getTotal());
        return model;
    }

    
    public Device findByUuid(String uuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/" + uuid, Device.class);
    }

    
    public CloudPlatformPageResult<Device> findByIds(List<Integer> ids) {
        CloudPlatformPageResult<Device> model = new CloudPlatformPageResult<>();
        model.setList(new ArrayList<>());
        
        for (int i = 0; i < ids.size(); i += 500) {
            int index = Math.min(i + 500, ids.size());
            List<Integer> newIds = ids.subList(i, index);
            CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/byid?ids=" + Joiner.on(",").join(newIds), CloudPlatformItemsPageResult.class);
            super.checkResult(pageResult);
            List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), Device.class);
            model.getList().addAll(list);
        }
        return model;
    }

    
    public BaseCloudPlatformResult deleteByIds(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", uuids);
        BaseCloudPlatformResult result = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/batch_delete",JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }
}
