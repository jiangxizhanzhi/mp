package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 靶标
 * @author wanggang
 */
@Service
@Slf4j
public class DeviceService extends BaseCpService {

    /**
     * 分页查询靶标信息
     */
    private static final String LIST_VIRTUAL_DEVICES = "/cloud/v1/device/virtual_device";

    /**
     * 根据uuid查询靶标的具体信息
     */
    private static final String GET_DEVICE_BY_UUID = "/cloud/v1/device/{uuid}";

    /**
     * 根据多个uuid批量查设备信息
     */
    private static final String LIST_DEVICES_BY_IDS = "/cloud/v1/device/byid";

    /**
     * 根据多个uuid批量删除虚拟靶标
     */
    private static final String DELETE_DEVICES_BY_UUIDS = "/cloud/v1/device/batch_delete";

    public DeviceService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询
     * @param params
     * @return
     */
    public List<Device> page(String params){

        CloudPlatformItemsPageResult<Device> pageResult = super.sendGet(LIST_VIRTUAL_DEVICES + params,
                new TypeReference<CloudPlatformItemsPageResult<Device>>() {
        });
        super.checkResult(pageResult);
        return pageResult.getData().getItems();
    }

    /**
     * 根据UUID查询
     * @param uuid
     * @return
     */
    public Device findByUuid(String uuid){
        String api = UriComponentsBuilder.fromPath(GET_DEVICE_BY_UUID).buildAndExpand(uuid).toUriString();
        return super.sendGet(api, Device.class);
    }

    /**
     * 根据ID集合查询
     * @param ids
     * @return
     */
    public List<Device> findByIds(List<Integer> ids) {
        List<Device> allDevices = new ArrayList<>();
        // 一次最多查询500个靶标信息
        for (int i = 0; i < ids.size(); i += 500) {
            int index = Math.min(i + 500, ids.size());
            List<Integer> newIds = ids.subList(i, index);
            Map<String, String> body = new HashMap<>();
            body.put("ids", Joiner.on(",").join(newIds));
            CloudPlatformItemsPageResult<Device> pageResult = super.sendPost(LIST_DEVICES_BY_IDS, JsonUtils.snakeStringify(body), new TypeReference<CloudPlatformItemsPageResult<Device>>() {
            });
            super.checkResult(pageResult);
            List<Device> list = pageResult.getData().getItems();
            allDevices.addAll(list);
        }
        return allDevices;
    }

    /**
     * 批量删除
     * @param uuids
     */
    public CloudPlatformResult deleteByIds(List<String> uuids) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuids", uuids);
        CloudPlatformResult result = super.sendDelete(DELETE_DEVICES_BY_UUIDS, JSONObject.toJSONString(jsonObject), CloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }
}
