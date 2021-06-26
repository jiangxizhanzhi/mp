package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-弹性IP
 * @author wanggang
 */
@Service
public class ElasticIpServiceImpl extends BaseOpenStackServiceImpl implements ElasticIpService {

    public ElasticIpServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public ElasticIpOpenStackModel page(ElasticIpPageRequest request) {
        return super.sendGet(URL_VERSION + "/networks/floatingips" + BeanUtils.wrapperGetParamsByObject(request),ElasticIpOpenStackModel.class);
    }
}
