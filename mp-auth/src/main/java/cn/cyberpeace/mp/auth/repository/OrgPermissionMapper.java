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

    List<TargetModel> listByOrgId(@Param("orgId") String orgId);

    void deleteByOrgId(@Param("orgId") String orgId);

    void deleteByTargetId(@Param("targetId") String targetId);
}
