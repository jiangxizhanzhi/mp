package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.RouterOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
public class RouterServiceImpl extends BaseOpenStackServiceImpl implements RouterService {

    public RouterServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public RouterOpenStackModel page(RouterPageRequest request) {
        Integer limit = request.getLimit();
        Integer offset = request.getOffset();
        return super.sendGet("/routers?" + "limit=" + limit + "&offset=" + offset,RouterOpenStackModel.class);
    }
}
