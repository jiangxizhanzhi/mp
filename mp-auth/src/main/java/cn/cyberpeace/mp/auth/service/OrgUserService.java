package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface OrgUserService extends BaseModelService<OrgUserModel> {

    List<UserModel> listUsersByOrgId(String orgId);

    List<OrgModel> listOrgsByUserId(String userId);

    // 增量添加组织-用户关系
    boolean addOrgUsers(String orgId, List<String> userIds);

}
