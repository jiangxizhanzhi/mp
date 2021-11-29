package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceStaticsCloudPlatformModel;

import java.util.List;

/**
 * 设备
 * @author wanggang
 */
public interface DeviceService extends BaseCloudPlatformService {

    String DEVICE = "/device";

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Device findById(String id);

    /**
     * 根据ID集合查询
     * @param ids
     * @return
     */
    DeviceCloudPlatformModel findByIds(List<Integer> ids);


    /**
     * 批量删除
     * @param ids
     */
    Boolean deleteByIds(List<String> ids);

    /**
     * 靶标统计
     * @return
     */
    DeviceStaticsCloudPlatformModel statics();
}
