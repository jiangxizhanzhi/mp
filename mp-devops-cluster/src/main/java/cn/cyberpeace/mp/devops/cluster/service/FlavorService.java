package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.FlavorCloudPlatformModel;

public interface FlavorService extends BaseCloudPlatformService {

    FlavorCloudPlatformModel page(String getParams);
}
