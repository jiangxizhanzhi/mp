package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface OrgUserService extends BaseModelService<OrgUserModel, QueryParams> {

    List<UserModel> listUsersByOrgId(String orgId);

    List<OrgModel> listOrgsByUserId(String userId);

    // 列出用户所在组织的层级关系
    List<OrgDto> listHierarchicalOrgsByUserId(String userId);

    // 增量添加组织-用户关系
    boolean additionalBindOrgUsers(String orgId, List<String> userIds);

    String getAllOrgPathByUserId(String userId);

    PageInfo<UserDto> pageUsersByOrg(OrgUserPageRequest req);

    // 清除用户所有组织
    boolean cleanUserOrgs(String userId);

}
