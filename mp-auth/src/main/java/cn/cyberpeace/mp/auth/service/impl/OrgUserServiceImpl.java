package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgUserMapper;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private QueryParentOrgDtoOperation queryParentOrgOperation;

    @Autowired
    private QueryChildOrgDtoOperation queryChildOrgDtoOperation;

    @Autowired
    private AuthDesensitizedService desensitizedService;

    @Autowired
    private OperateUserPermissionService opUserPermCheckService;

//    @Autowired
//    private QueryChildOrgModelListOperation queryChildOrgModelListOperation;

    @Autowired
    public OrgUserServiceImpl(OrgUserMapper modelMapper, OrgUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<UserModel> listUsersByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        List<UserModel> users = getModelMapper().listUsersByOrgId(orgId, getExcludeRoleIdsByLoginUserForSearching());
        desensitizedService.desensitiveUserModels(users);
        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds) {
        log.info("orgId:{}, uname:{}", orgId, uname);

        // 默认查出orgId下所有子组织的用户
        List<String> orgIds = new ArrayList<>();
        // 先添加父级id
        if (StringUtils.isNotBlank(orgId)) {
            orgIds.add(orgId);
            List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevel(orgId);
            orgIds.addAll(childOrgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        }

        List<UserModel> users = getModelMapper().listUsers(orgIds, uname, userIds, getExcludeRoleIdsByLoginUserForSearching());
        desensitizedService.desensitiveUserModels(users);
        return users;
    }

    @Override
    @Cacheable(value = "orgs_cache", key = "'list_orgs_byuserid'+#userId")
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
        opUserPermCheckService.checkByTargetUserIds(userIds);
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
    public PageInfo<UserDto> pageUsersByOrg(OrgUserPageRequest req) {

        List<String> idsList = null;
        if (Objects.nonNull(req.getIds()) && req.getIds().length > 0) {
            idsList = Arrays.asList(req.getIds());
        }

        List<String> orgIds = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getOrg())) {
            // 添加父级org
            orgIds.add(req.getOrg());
            if (req.isShowAllUnderLevel()) {
                List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevel(req.getOrg());
                if (CollectionUtil.isNotEmpty(childOrgDtos)) {
                    orgIds.addAll(childOrgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
                }
            }
        }

        log.info("orgIds: {}", orgIds);

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserModel> users = getModelMapper().listUsers(orgIds, req.getUsername(), idsList, getExcludeRoleIdsByLoginUserForSearching());
        PageInfo pageInfo = new PageInfo(users);

        List<UserDto> userDtos = users.stream().map(u -> {
            UserDto userDto = userService.userModel2UserDto(u);
            return userDto;
        }).collect(Collectors.toList());

        desensitizedService.desensitiveUserDtos(userDtos);
        pageInfo.setList(userDtos);
        return pageInfo;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        Assert.notNull(userId, "userId不能为空");

//        var objResult = userService.get(userId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
//        }

        getModelMapper().deleteByUserId(userId);

        return true;
    }

    @Override
    public boolean deleteByOrgId(String orgId) {
        Assert.notNull(orgId, "orgId不能为空");

//        var objResult = orgService.get(orgId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
//        }

        getModelMapper().deleteByOrgId(orgId);

        return true;
    }


    private List<String> getExcludeRoleIdsByLoginUserForSearching() {

        boolean isSupserUser = opUserPermCheckService.isLoginUserSuperUser();
        if (isSupserUser) {
            // 超级用户不受限制
            return Collections.emptyList();
        }

        boolean isAdmin = opUserPermCheckService.isLoginUserAdmin();
        if (isAdmin) {
            // 管理员不可查询同级管理员和超级用户
            return Arrays.asList(AuthConstant.ROLE_SUPERUSER_ID, AuthConstant.ROLE_ADMIN_ID);
        }

        // 普通用户不可查询管理员和超级用户
        return Arrays.asList(AuthConstant.ROLE_SUPERUSER_ID, AuthConstant.ROLE_ADMIN_ID);
    }

}
