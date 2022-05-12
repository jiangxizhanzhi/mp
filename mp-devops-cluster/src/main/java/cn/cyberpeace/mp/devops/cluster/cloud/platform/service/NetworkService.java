package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
@Slf4j
public class NetworkService extends BaseOpenStackService {

    private static final String NETWORKS = "/networks";

    public NetworkService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<NetworkModel> page(String getParams){
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + getParams, CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<NetworkModel> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()), NetworkModel.class);
        CloudPlatformPageModel<NetworkModel> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<ElasticIpModel> pageElasticIp(String getParams){
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + "/floatingips" + getParams, CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<ElasticIpModel> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()), ElasticIpModel.class);
        CloudPlatformPageModel<ElasticIpModel> model = new CloudPlatformPageModel<>();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     *
     * @return
     */
    public CloudPlatformPageModel<NetworkAgentModel> agentsStateFromNetworks(){
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + NETWORKS + "/agents", CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<NetworkAgentModel> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()), NetworkAgentModel.class);
        CloudPlatformPageModel<NetworkAgentModel> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
