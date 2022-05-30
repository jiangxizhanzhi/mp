package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.auth.base.repository.BasePermissionMapper;
import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devops.cluster.po.TargetAuthPO;
import cn.cyberpeace.mp.devops.cluster.po.TargetPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@PermissionModelClass(permissionClass = TargetAuthPO.class, targetClass = TargetPO.class)
@ModelClass(TargetAuthPO.class)
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
    List<TargetAuthPO> findByTarget(String target);

    /**
     * 根据镜像ID集合批量删除数据
     * @param ids
     * @return
     */
    Integer deleteByTargets(@Param("ids") List<String> ids);
}
