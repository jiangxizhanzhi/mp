package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ElasticIpOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;

/**
 * 弹性IP接口层
 */
public interface ElasticIpService extends BaseOpenStackService  {

    ElasticIpOpenStackModel page(ElasticIpPageRequest request);
}
