package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.NetworkPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public NetworkCloudPlatformModel page(String getParams) {
        NetworkPageResult pageResult = super.sendGet(URL_VERSION + "/networks" + getParams, NetworkPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<NetworkModel> data = pageResult.getData();
        List<NetworkModel> list = data.getList();
        NetworkCloudPlatformModel model = new NetworkCloudPlatformModel();
        model.setNetworks(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }


}
