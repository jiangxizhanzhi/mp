package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.devopscluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DevopsTargetAuthService {

    private final DeviceTargetAuthMapper deviceTargetAuthMapper;

    
    public List<ImageAuthTotalCount> totalCount(List<String> ids){
        return deviceTargetAuthMapper.totalCount(ids);
    }

    
    public List<DeviceTargetAuthPO> findByTarget(String target){
        return deviceTargetAuthMapper.findByTarget(target);
    }

    
    @Transactional
    public Integer deleteByTargets(List<String> ids){
        return deviceTargetAuthMapper.deleteByTargets(ids);
    }

    
    @Transactional
    public int bulkCreate(List<DeviceTargetAuthPO> authModelList){
        return deviceTargetAuthMapper.bulkCreate(authModelList).getSuccessCount();
    }
}
