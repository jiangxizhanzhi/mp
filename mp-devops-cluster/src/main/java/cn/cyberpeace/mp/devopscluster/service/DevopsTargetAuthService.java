package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.devopscluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 靶标管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class DevopsTargetAuthService {

    private final DeviceTargetAuthMapper deviceTargetAuthMapper;

    /**
     * 分组查询镜像授权用户数
     * @param ids
     * @return
     */
    public List<ImageAuthTotalCount> totalCount(List<String> ids){
        return deviceTargetAuthMapper.totalCount(ids);
    }

    /**
     * 根据镜像ID查询用户信息
     * @param target
     * @return
     */
    public List<DeviceTargetAuthPO> findByTarget(String target){
        return deviceTargetAuthMapper.findByTarget(target);
    }

    /**
     * 根据镜像ID集合批量删除数据
     * @param ids
     * @return
     */
    @Transactional
    public Integer deleteByTargets(List<String> ids){
        return deviceTargetAuthMapper.deleteByTargets(ids);
    }

    /**
     * 批量创建
     * @param authModelList
     * @return
     */
    @Transactional
    public int bulkCreate(List<DeviceTargetAuthPO> authModelList){
        return deviceTargetAuthMapper.bulkCreate(authModelList);
    }
}
