package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgUserMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.OrgUserFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrgUserServiceImpl extends BaseModelServiceImpl<OrgUserModel, OrgUserMapper, OrgUserFields, QueryParams>
        implements OrgUserService {

    @Autowired
    private OrgService orgService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private QueryParentOrgOperation queryParentOrgOperation;

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
    public List<OrgDto> listHierarchicalOrgsByUserId(String userId) {
        List<OrgModel> orgs = getModelMapper().listOrgsByUserId(userId);
        List<OrgDto> orgDtos = new ArrayList<>();
        orgs.forEach(o -> {
            OrgDto orgDto = new OrgDto();
            BeanUtils.copyProperties(o, orgDto);
            log.warn("queryParentOrgOperation:{}", queryParentOrgOperation);
            orgDto.execute(queryParentOrgOperation);
            orgDtos.add(orgDto);
        });

        return orgDtos;
    }


    @Override
    public boolean additionalBindOrgUsers(String orgId, List<String> userIds) {
        Assert.notBlank(orgId, "orgId不能为空！");
        var objResult = orgService.get(orgId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
        }

        // 不清楚组织-用户关系，而是增量添加

        if (CollectionUtil.isEmpty(userIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加组织-用户关系
        userIds.forEach(uid -> {
            // todo 校验用户是否存在
            OrgUserModel orguser = new OrgUserModel();
            orguser.setOrg(orgId);
            orguser.setUser(uid);
            orguser.setTenant(AuthConstant.DEFAULT_TENANT);
        });

        return true;

    }

    @Override
    public String getAllOrgPathByUserId(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        List<OrgModel> orgs = listOrgsByUserId(userId);
//        String allorgpath = orgs.stream().map(o -> orgService.calculatePathByOrgId(o.getId())).collect(Collectors.joining(","));
        String allorgpath = orgs.stream().map(OrgModel::getPath).collect(Collectors.joining(","));
        return allorgpath;
    }

    @Override
    public PageInfo<UserModel> pageUsersByOrg(OrgUserPageRequest req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserModel> users = getModelMapper().listUsers(req.getOrg(), req.getUsername());
        PageInfo pageInfo = new PageInfo(users);

        List<UserDto> userDtos = users.stream().map(u -> {
            userService.processExpiredStatus(u);

            UserDto userDto = new UserDto();
            cn.cyberpeace.mp.base.util.BeanUtils.copyProperties(u, userDto);
            String allorgpath = getAllOrgPathByUserId(u.getId());
            userDto.setOrgpath(allorgpath);

            List<RoleModel> roles = userRoleService.listByUserId(u.getId());
            userDto.setRoles(roles);
            return userDto;
        }).collect(Collectors.toList());

        pageInfo.setList(userDtos);
        return pageInfo;
    }

    @Override
    public boolean cleanUserOrgs(String userId) {
        Assert.notNull(userId, "userid不能为空");

        var objResult = userService.get(userId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
        }

        getModelMapper().deleteByUser(userId);

        return true;
    }


}
