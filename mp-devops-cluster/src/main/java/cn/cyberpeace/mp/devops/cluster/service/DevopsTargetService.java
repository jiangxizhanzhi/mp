package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;

import java.util.List;

/**
 * 靶标管理
 * @author wanggang
 */
public interface DevopsTargetService {

    /**
     * 更新
     * @param device
     * @return
     */
    Integer updateById(Device device);

    /**
     * 新增
     * @param deviceList
     * @return
     */
    Integer insert(List<Device> deviceList);
}
