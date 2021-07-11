package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.authbase.repository.BasePermissionMapper;
import cn.cyberpeace.mp.authbase.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@PermissionModelClass(permissionClass = DeviceTargetAuthPO.class, targetClass = DeviceTargetPO.class)
@ModelClass(DeviceTargetAuthPO.class)
public interface DeviceTargetAuthMapper extends BasePermissionMapper {

    
    List<ImageAuthTotalCount> totalCount(@Param("ids") List<String> ids);

    
    List<DeviceTargetAuthPO> findByTarget(String target);

    
    Integer deleteByTargets(@Param("ids") List<String> ids);
}
