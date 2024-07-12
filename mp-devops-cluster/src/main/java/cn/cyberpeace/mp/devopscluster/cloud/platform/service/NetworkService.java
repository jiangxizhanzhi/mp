 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkService.java.
 // File size 2894.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devopscluster.model.common.VlanIpStockModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @ClassName NetworkService
 * @Description 运维管控-系统运维-网络实例管理-网络
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class NetworkService extends BaseCpService {

    private static final String LIST_NETWORKS = "/cloud/v1/network";

    private static final String NETWORKS = "/networks";


    /**
     * NetworkService
     *
     * @param devopsClusterConfig
     */
    public NetworkService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<NetworkModel> page(String getParams){
        return super.page(LIST_NETWORKS + getParams, NetworkModel.class);
    }


    /**
     * 查询网络vlan ip信息
     * @param getParams
     * @return
     */
    @Deprecated
    public CloudPlatformResult<VlanIpStockModel> vlanIpStock(String getParams){
        CloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/vlan_ip_stock" + getParams, CloudPlatformResult.class);
        super.checkResult(result);
        VlanIpStockModel data = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), VlanIpStockModel.class);
        CloudPlatformResult cpresult = new CloudPlatformResult();
        cpresult.setData(data);
        cpresult.setCode(result.getCode());
        return cpresult;
    }

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    @Deprecated
    public CloudPlatformPageResult<ElasticIpModel> pageElasticIp(String getParams){
        return new CloudPlatformPageResult<>(Collections.emptyList());
    }

    /**
     *
     * @return
     */
    @Deprecated
    public CloudPlatformPageResult<NetworkAgentModel> agentsStateFromNetworks(){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/agents", NetworkAgentModel.class);
    }
}
