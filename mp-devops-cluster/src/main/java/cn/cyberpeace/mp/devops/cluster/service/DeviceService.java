package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;

import java.util.List;

/**
 * 设备
 * @author wanggang
 */
public interface DeviceService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    DeviceCloudPlatformModel page(String getParams);

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
    DeviceCloudPlatformModel findByIds(List<String> ids);


    /**
     * 批量删除
     * @param ids
     */
    Boolean deleteByIds(List<String> ids);
}
