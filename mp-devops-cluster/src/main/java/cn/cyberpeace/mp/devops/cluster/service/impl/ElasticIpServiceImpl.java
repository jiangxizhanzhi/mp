package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-弹性IP
 * @author wanggang
 */
@Service
public class ElasticIpServiceImpl extends BaseOpenStackServiceImpl implements ElasticIpService {

    public ElasticIpServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public ElasticIpCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/networks/floatingips" + getParams, ElasticIpCloudPlatformModel.class);
    }


}
