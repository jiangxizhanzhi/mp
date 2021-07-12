package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class InstanceServiceImpl extends BaseOpenStackServiceImpl implements InstanceService {

    public InstanceServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(InstancePageRequest::getOffset,
                InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }

    @Override
    public InstanceCloudPlatformModel page(InstancePageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        return super.sendGet(URL_VERSION + "/instances" + BeanUtils.wrapperGetParamsByObject(request,ignoreFields), InstanceCloudPlatformModel.class);
    }

    @Override
    public Integer getTotalInstances(InstancePageRequest request) {
        InstanceCloudPlatformModel page = this.page(request,this.getTotalPageIgnoreField());
        return Objects.isNull(page) ||  Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal() ;
    }


}
