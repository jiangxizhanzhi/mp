package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(OrgPermissionModel.class)
public interface OrgPermissionMapper extends BaseModelMapper<OrgPermissionModel> {


    @Select("select distinct * from mp_auth_target where id in " +
            "(select target from mp_auth_org_permission where owner = #{orgId})")
    List<TargetModel> listByOrgId(@Param("orgId") String orgId);


    @Delete("delete from mp_auth_org_permission where owner = #{orgId}")
    void deleteByOrg(@Param("orgId") String orgId);

    @Delete("delete from mp_auth_org_permission where target = #{targetId}")
    void deleteByTargetId(@Param("targetId") String targetId);
}
