package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.ElasticIpPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.NetworkAgentPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.NetworkPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.NetworkAgentCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
@Slf4j
public class NetworkService extends BaseOpenStackServiceImpl {

    private static final String NETWORKS = "/networks";

    public NetworkService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    public NetworkCloudPlatformModel page(String getParams){
        NetworkPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + getParams, NetworkPageResult.class);
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

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    public ElasticIpCloudPlatformModel pageElasticIp(String getParams){
        ElasticIpPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + "/floatingips" + getParams, ElasticIpPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<ElasticIpModel> data = pageResult.getData();
        List<ElasticIpModel> list = data.getList();
        ElasticIpCloudPlatformModel model = new ElasticIpCloudPlatformModel();
        model.setFloatingips(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     *
     * @return
     */
    public NetworkAgentCloudPlatformModel agentsStateFromNetworks(){
        NetworkAgentPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + "/agents", NetworkAgentPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<NetworkAgentModel> data = pageResult.getData();
        List<NetworkAgentModel> list = data.getList();
        NetworkAgentCloudPlatformModel model = new NetworkAgentCloudPlatformModel();
        model.setAgents(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
