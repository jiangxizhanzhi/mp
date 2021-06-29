package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 运维管控-系统运维-网络实例管理-弹性IP
 * @author wanggang
 */
@Service
public class ElasticIpServiceImpl extends BaseOpenStackServiceImpl implements ElasticIpService {

    public ElasticIpServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public ElasticIpCloudPlatformModel page(ElasticIpPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        return super.sendGet(URL_VERSION + "/networks/floatingips" + BeanUtils.wrapperGetParamsByObject(request,ignoreFields), ElasticIpCloudPlatformModel.class);
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(ElasticIpPageRequest::getPage,ElasticIpPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(ElasticIpPageRequest::getLimit,ElasticIpPageRequest::getOffset,
                ElasticIpPageRequest::getPage,ElasticIpPageRequest::getPerPage);
    }
}
