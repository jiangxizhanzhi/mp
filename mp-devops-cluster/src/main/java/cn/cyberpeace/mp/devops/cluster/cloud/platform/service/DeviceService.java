package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
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
    public CloudPlatformPageResult<Device> page(String params){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/lists" + params, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Device.class);
        CloudPlatformPageResult<Device> model = new CloudPlatformPageResult<>();
        model.setList(list);
        model.setTotal(pageResult.getTotal());
        return model;
    }

    /**
     * 根据UUID查询
     * @param uuid
     * @return
     */
    public Device findByUuid(String uuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + DEVICE + "/" + uuid, Device.class);
    }

    /**
     * 根据ID集合查询
     * @param ids
     * @return
     */
    public CloudPlatformPageResult<Device> findByIds(List<Integer> ids){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION +  DEVICE + "/byid?ids=" + Joiner.on(",").join(ids), CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Device.class);
        CloudPlatformPageResult<Device> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    /**
     * 批量删除
     * @param uuids
     */
    public BaseCloudPlatformResult deleteByIds(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", uuids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/batch_delete",JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
    }
}
