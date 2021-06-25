package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.ContainerOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class ContainerServiceImpl extends BaseOpenStackServiceImpl implements ContainerService {

    public ContainerServiceImpl(OpenStackConfig openStackConfig,ObjectMapper objectMapper) {
        super(openStackConfig,objectMapper);
    }


    @Override
    public ContainerOpenStackModel page(ContainerPageRequest request) {
        Integer limit = request.getLimit();
        Integer offset = request.getOffset();
        return super.sendGet("/containers?" + "limit=" + limit + "&offset=" + offset,ContainerOpenStackModel.class);
    }
}
