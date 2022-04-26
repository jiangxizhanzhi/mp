package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标
 * @author wanggang
 */
@Service
@Slf4j
public class DeviceService extends BaseOpenStackService {

    private static final String DEVICE = "/device";

    public DeviceService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param params
     * @return
     */
    public DeviceCloudPlatformModel page(String params){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/lists" + params, DeviceCloudPlatformModel.class);
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public Device findById(String id){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/" + id, Device.class);
    }

    /**
     * 根据ID集合查询
     * @param ids
     * @return
     */
    public DeviceCloudPlatformModel findByIds(List<Integer> ids){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION +  DEVICE + "/byid?ids=" + Joiner.on(",").join(ids), DeviceCloudPlatformModel.class);
    }

    /**
     * 批量删除
     * @param uuids
     */
    public BaseCloudPlatformModel deleteByIds(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", uuids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/batch_delete",JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
    }
}
