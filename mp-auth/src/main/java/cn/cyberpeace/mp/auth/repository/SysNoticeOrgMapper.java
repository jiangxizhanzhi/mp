package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysNoticeOrgModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysNoticeOrgModel.class)
public interface SysNoticeOrgMapper extends BaseModelMapper<SysNoticeOrgModel> {

    List<OrgModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByOrg(@Param("orgId") String orgId);

}
