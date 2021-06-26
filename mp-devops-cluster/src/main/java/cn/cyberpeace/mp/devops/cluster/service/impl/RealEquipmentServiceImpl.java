package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.RealEquipmentOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 实装管理接口
 */
@Service
public class RealEquipmentServiceImpl extends BaseServiceImpl implements RealEquipmentService {

    public RealEquipmentServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public RealEquipmentOpenStackModel page() {
        return super.sendGet(openStackConfig.getMg_url().concat("/api/cr/api/admin/scene/devices/?entity=true"),RealEquipmentOpenStackModel.class);
    }

    @Override
    public Integer getTotalRealEquipment() {
        RealEquipmentOpenStackModel page = this.page();
        return Objects.isNull(page) || Objects.isNull(page.getLinks()) || Objects.isNull(page.getLinks().getPagination())
               || Objects.isNull(page.getLinks().getPagination().getTotal()) ? 0 : page.getLinks().getPagination().getTotal();
    }
}
