package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;

/**
 * 弹性IP接口层
 */
public interface ElasticIpService extends BaseCloudPlatformService {

    /**
     * 分页查询弹性IP信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    ElasticIpCloudPlatformModel page(ElasticIpPageRequest request, String... ignoreFields);
}
