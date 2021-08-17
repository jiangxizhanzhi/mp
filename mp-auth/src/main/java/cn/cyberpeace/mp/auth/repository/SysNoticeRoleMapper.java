package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysNoticeRoleModel.class)
public interface SysNoticeRoleMapper extends BaseModelMapper<SysNoticeRoleModel> {

    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByRole(@Param("roleId") String roleId);

}
