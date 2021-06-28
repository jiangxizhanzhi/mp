package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(OrgPermissionModel.class)
public interface OrgPermissionMapper extends BaseModelMapper<OrgPermissionModel> {

    @Delete("delete from mp_auth_org_permission where owner = #{orgId}")
    void deleteByOrg(@Param("orgId") String orgId);
}
