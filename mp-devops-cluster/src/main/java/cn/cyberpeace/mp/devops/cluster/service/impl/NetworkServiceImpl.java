package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.NetworkOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
public class NetworkServiceImpl extends BaseOpenStackServiceImpl implements NetworkService {

    public NetworkServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public NetworkOpenStackModel page(NetworkPageRequest request) {
        return super.sendGet(URL_VERSION + "/networks" + BeanUtils.wrapperGetParamsByObject(request),NetworkOpenStackModel.class);
    }
}
