package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;

/**
 * 弹性IP接口层
 */
public interface ElasticIpService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @param request
     * @param ignoreFields 忽略的字段
     * @return
     */
    ElasticIpCloudPlatformModel page(ElasticIpPageRequest request, String... ignoreFields);
}
