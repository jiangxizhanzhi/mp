package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgUserMapper;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.fields.OrgUserFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgUserServiceImpl extends BaseModelServiceImpl<OrgUserModel, OrgUserMapper, OrgUserFields>
        implements OrgUserService {

    @Autowired
    public OrgUserServiceImpl(OrgUserMapper modelMapper, OrgUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<UserModel> listUsersByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        List<UserModel> users = getModelMapper().listUsersByOrgId(orgId);

        return users;
    }

    @Override
    public List<OrgModel> listOrgsByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        List<OrgModel> orgs = getModelMapper().listOrgsByUserId(userId);

        return orgs;
    }

    @Override
    public boolean addOrgUsers(String orgId, List<String> userIds) {
        return false;
    }


}
