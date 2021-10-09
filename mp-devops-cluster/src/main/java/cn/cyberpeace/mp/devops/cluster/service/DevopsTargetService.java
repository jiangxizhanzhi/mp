package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;

/**
 * 靶标管理
 * @author wanggang
 */
public interface DevopsTargetService {

    /**
     * 新增
     * @param device
     * @return
     */
    Integer insert(Device device);

    /**
     * 更新
     * @param device
     * @return
     */
    Integer updateById(Device device);
}
