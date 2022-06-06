package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetAuthPO;

import java.util.List;

/**
 * 靶标管理
 * @author wanggang
 */
public interface DevopsTargetAuthService {

    /**
     * 分组查询镜像授权用户数
     * @param ids
     * @return
     */
    List<ImageAuthTotalCount> totalCount(List<String> ids);

    /**
     * 根据镜像ID查询用户信息
     * @param target
     * @return
     */
    List<DeviceTargetAuthPO> findByTarget(String target);

    /**
     * 根据镜像ID集合批量删除数据
     * @param ids
     * @return
     */
    Integer deleteByTargets(List<String> ids);

    /**
     * 批量创建
     * @param authModelList
     * @return
     */
    int bulkCreate(List<DeviceTargetAuthPO> authModelList);
}
