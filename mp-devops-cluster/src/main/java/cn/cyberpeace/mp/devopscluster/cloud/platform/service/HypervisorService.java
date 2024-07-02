package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.cscene.response.HostStatisticsResponse;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.HypervisorWeightAndMaxInstanceCloudPlatformResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName HypervisorService
 * @Description 云平台监控
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class HypervisorService extends BaseCpService {

    private static final String HOSTS_MONITOR_LIST = "/cloud/v1/monitor/hosts";

    public static final String HYPERVISORS = "/hypervisors";

    /**
     * HypervisorService
     *
     * @param devopsClusterConfig
     */
    public HypervisorService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }


    /**
     * page
     *
     * @param getParams
     * @return
     */
    public HostStatisticsResponse page(String getParams) {
        return super.sendGet(HOSTS_MONITOR_LIST + getParams, new TypeReference<HostStatisticsResponse>() {
        });
    }
    /**
     * 分页查询节点管理信息
     * @param getParams
     * @return
     */


    /**
     * 获取虚拟机数量限制
     * @return
     */
    public HypervisorWeightAndMaxInstanceCloudPlatformResult getWeightAndMaxInstances(String getParams){
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + "/vm_status" + getParams, HypervisorWeightAndMaxInstanceCloudPlatformResult.class);
    }

    /**
     * 节点管理-重启/关机
     */
    public void rebootOrPowerOff(String json){
        super.sendPost(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + "/compute_control", json);
    }

}
