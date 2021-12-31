package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceStaticsCloudPlatformModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 靶标
 * @author wanggang
 */
@Service
@Slf4j
public class DeviceService extends BaseOpenStackServiceImpl {

    private static final String DEVICE = "/device";

    public DeviceService(DevopsConfig devopsConfig) {
        super(devopsConfig);
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
    public Boolean deleteByIds(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", uuids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/batch_delete",JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
        return model.isSuccess();
    }

    /**
     * 靶标统计
     * @return
     */
    public DeviceStaticsCloudPlatformModel statics() {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/statics", DeviceStaticsCloudPlatformModel.class);
    }
}
