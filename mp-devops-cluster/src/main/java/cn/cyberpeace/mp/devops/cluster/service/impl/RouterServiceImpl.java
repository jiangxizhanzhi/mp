package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
public class RouterServiceImpl extends BaseOpenStackServiceImpl implements RouterService {

    public RouterServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public RouterCloudPlatformModel page(RouterPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        return super.sendGet(URL_VERSION + "/routers" + BeanUtils.wrapperGetParamsByObject(request,ignoreFields), RouterCloudPlatformModel.class);
    }

    @Override
    public Integer getTotalRouters(RouterPageRequest request) {
        RouterCloudPlatformModel page = this.page(request,this.getTotalPageIgnoreField());
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? 0 : page.getTotal();
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(InstancePageRequest::getLimit,InstancePageRequest::getOffset,
                InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }
}
