package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class InstanceServiceImpl extends BaseOpenStackServiceImpl implements InstanceService {

    public InstanceServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig,objectMapper);
    }

    @Override
    public InstanceOpenStackModel page(InstancePageRequest request) {
        Integer limit = request.getLimit();
        Integer offset = request.getOffset();
        return super.sendGet("/instances?" + "limit=" + limit + "&offset=" + offset,InstanceOpenStackModel.class);
    }
}
