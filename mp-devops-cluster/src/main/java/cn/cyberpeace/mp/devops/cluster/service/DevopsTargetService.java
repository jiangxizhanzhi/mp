package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;

public interface DevopsTargetService {

    Integer insert(Device device);

    Integer updateById(Device device);
}
