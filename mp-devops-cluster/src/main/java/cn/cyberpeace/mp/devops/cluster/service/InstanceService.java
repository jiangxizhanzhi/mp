package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
public interface InstanceService extends BaseCloudPlatformService {

    /**
     * 分页查询虚拟机信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    InstanceCloudPlatformModel page(InstancePageRequest request, String... ignoreFields);

    /**
     * 获取虚拟机总数
     * @return
     */
    Integer getTotalInstances(InstancePageRequest request);
}
