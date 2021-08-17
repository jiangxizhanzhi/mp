package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.RealEquipmentOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 实装管理接口
 */
@Service
public class RealEquipmentServiceImpl extends BaseServiceImpl implements RealEquipmentService {

    public RealEquipmentServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public RealEquipmentOpenStackModel page() {
        return super.sendGet(devopsConfig.getCpUrl().concat("/api/cr/api/admin/scene/devices/?entity=true&per_page=1"), JwtCommon.getTokenHeader(),RealEquipmentOpenStackModel.class);
    }

    @Override
    public Integer getTotalRealEquipment() {
        RealEquipmentOpenStackModel page = this.page();
        return Objects.isNull(page) || Objects.isNull(page.getLinks()) || Objects.isNull(page.getLinks().getPagination())
               || Objects.isNull(page.getLinks().getPagination().getTotal()) ? NumberUtils.INTEGER_ZERO : page.getLinks().getPagination().getTotal();
    }

}
