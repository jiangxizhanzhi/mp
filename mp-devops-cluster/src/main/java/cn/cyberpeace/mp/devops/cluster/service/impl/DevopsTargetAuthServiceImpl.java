package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetAuthMapper;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 靶标管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class DevopsTargetAuthServiceImpl implements DevopsTargetAuthService {

    private final DeviceTargetAuthMapper deviceTargetAuthMapper;

    /**
     * 分组查询镜像授权用户数
     * @param ids
     * @return
     */
    @Override
    public List<ImageAuthTotalCount> totalCount(List<String> ids){
        return deviceTargetAuthMapper.totalCount(ids);
    }

    /**
     * 根据镜像ID查询用户信息
     * @param target
     * @return
     */
    @Override
    public List<DeviceTargetAuthPO> findByTarget(String target){
        return deviceTargetAuthMapper.findByTarget(target);
    }

    /**
     * 根据镜像ID集合批量删除数据
     * @param ids
     * @return
     */
    @Override
    public Integer deleteByTargets(List<String> ids){
        return deviceTargetAuthMapper.deleteByTargets(ids);
    }

    /**
     * 批量创建
     * @param authModelList
     * @return
     */
    @Override
    public int bulkCreate(List<DeviceTargetAuthPO> authModelList){
        return deviceTargetAuthMapper.bulkCreate(authModelList);
    }
}
