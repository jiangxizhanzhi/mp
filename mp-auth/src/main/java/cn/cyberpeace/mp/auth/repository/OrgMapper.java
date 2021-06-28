package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(OrgModel.class)
public interface OrgMapper extends BaseModelMapper<OrgModel> {

    @Select("SELECT * FROM mp_auth_org where parent = '' or parent is null")
    List<OrgModel> listParents();

    @Select("SELECT * FROM mp_auth_org where parent = #{parentId}")
    List<OrgModel> listByParentId(@Param("parentId") String parentId);



    @Select("SELECT * FROM mp_auth_org where tenant = #{tenantId}")
    List<OrgModel> listAll(@Param("tenantId") String tenantId);

}
