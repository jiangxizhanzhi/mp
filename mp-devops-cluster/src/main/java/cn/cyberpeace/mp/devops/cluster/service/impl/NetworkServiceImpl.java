package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
public class NetworkServiceImpl extends BaseOpenStackServiceImpl implements NetworkService {

    public NetworkServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(NetworkPageRequest::getPage,NetworkPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(NetworkPageRequest::getLimit,NetworkPageRequest::getOffset,
                NetworkPageRequest::getPage,NetworkPageRequest::getPerPage);
    }

    @Override
    public NetworkCloudPlatformModel page(NetworkPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        return super.sendGet(URL_VERSION + "/networks" + BeanUtils.wrapperGetParamsByObject(request,ignoreFields), NetworkCloudPlatformModel.class);
    }


}
