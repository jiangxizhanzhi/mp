package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.RouterPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public RouterCloudPlatformModel page(String getParams) {
        RouterPageResult pageResult = super.sendGet(URL_VERSION + "/routers" + getParams, RouterPageResult.class);

        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<RouterModel> data = pageResult.getData();
        List<RouterModel> routerModels = data.getList();
        RouterCloudPlatformModel model = new RouterCloudPlatformModel();
        model.setRouters(CollectionUtils.isEmpty(routerModels) ? Lists.newArrayList() : routerModels);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

}
