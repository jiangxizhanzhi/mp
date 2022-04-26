package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.RouterPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
@Slf4j
public class RouterService extends BaseOpenStackService {

    public RouterService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询路由信息
     * @param getParams
     * @return
     */
    public RouterCloudPlatformModel page(String getParams) {
        RouterPageResult pageResult = super.sendGet(URL_VERSION + "/routers" + getParams, RouterPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageResult<RouterModel> data = pageResult.getData();
        List<RouterModel> routerModels = data.getList();
        RouterCloudPlatformModel model = new RouterCloudPlatformModel();
        model.setRouters(CollectionUtils.isEmpty(routerModels) ? Lists.newArrayList() : routerModels);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
