package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 资源统计
 */
@Service
public class HypervisorServiceImpl extends BaseOpenStackServiceImpl
        implements HypervisorService {


    public HypervisorServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public HypervisorStatisticsOpenStackModel statistics() {
        return super.sendGet("/hypervisors/statistics",HypervisorStatisticsOpenStackModel.class);
    }

    @Override
    public HypervisorOpenStackModel page() {
        return super.sendGet("/hypervisors",HypervisorOpenStackModel.class);
    }
}
