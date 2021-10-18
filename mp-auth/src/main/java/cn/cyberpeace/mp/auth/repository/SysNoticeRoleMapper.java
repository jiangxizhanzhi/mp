package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.SysNoticeRoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(SysNoticeRoleModel.class)
public interface SysNoticeRoleMapper extends BaseModelMapper<SysNoticeRoleModel> {

    List<RoleModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByRole(@Param("roleId") String roleId);

}
