package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;

import java.util.List;

public interface HypervisorConverter extends BaseConverter {

    /**
     * 资源统计
     * @return
     */
    HypervisorStatisticsConvertModel statistics();

    /**
     * 分页查询节点管理信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    HypervisorPageConvertModel page(HypervisorPageRequest request, String... ignoreFields);

    /**
     * 获取虚拟机数量限制
     * @return
     */
    HypervisorWeightAndMaxInstanceConvertModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request);

    /**
     * 节点管理-重启/关机
     */
    void rebootOrPowerOff(HypervisorRequest request);

    /**
     * 服务状态监控
     */
    List<HypervisorServiceStateConvertModel> serviceState();

    /**
     * 查询虚拟结合交换机状态列表
     * @return
     */
    VirtualRealSwitchConvertModel listVirtualRealSwitch();
}
