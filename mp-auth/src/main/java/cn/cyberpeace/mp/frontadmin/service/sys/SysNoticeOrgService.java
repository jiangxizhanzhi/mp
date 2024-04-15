package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeOrgModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 */
public interface SysNoticeOrgService extends BaseModelService<SysNoticeOrgModel, QueryParams> {

    List<OrgModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByOrg(@Param("orgId") String orgId);
}
