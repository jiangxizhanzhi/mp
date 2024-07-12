 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceTargetAuthMapper.java.
 // File size 1506.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName DeviceTargetAuthMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePermissionMapper
 */
@Mapper
@Repository
@PermissionModelClass(permissionClass = DeviceTargetAuthPO.class, targetClass = DeviceTargetPO.class)
@ModelClass(DeviceTargetAuthPO.class)
public interface DeviceTargetAuthMapper extends BasePermissionMapper {

    /**
     * 分组查询镜像授权用户数
     * @param ids
     * @return
     */
    List<ImageAuthTotalCount> totalCount(@Param("ids") List<String> ids);

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
    Integer deleteByTargets(@Param("ids") List<String> ids);
}
