package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        return super.sendGet(URL_VERSION + "/instances" + BeanUtils.wrapperGetParamsByObject(request),InstanceOpenStackModel.class);
    }

    @Override
    public Integer getTotalInstances(InstancePageRequest request) {
        InstanceOpenStackModel page = this.page(request);
        return Objects.isNull(page) ||  Objects.isNull(page.getTotal()) ? 0 : page.getTotal() ;
    }
}
