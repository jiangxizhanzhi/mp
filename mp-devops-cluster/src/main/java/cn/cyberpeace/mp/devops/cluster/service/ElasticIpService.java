package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;

/**
 * 弹性IP接口层
 */
public interface ElasticIpService extends BaseCloudPlatformService {

    /**
     * 分页查询弹性IP信息
     * @param getParams
     * @return
     */
    ElasticIpCloudPlatformModel page(String getParams);
}
