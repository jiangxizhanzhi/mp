package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserTmpDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgUserMapper;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.OrgUserFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class OrgUserServiceImpl extends BaseModelServiceImpl<OrgUserModel, OrgUserMapper, OrgUserFields, QueryParams>
        implements OrgUserService {

    @Autowired
    private OrgService orgService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private QueryParentOrgDtoOperation queryParentOrgOperation;

    @Autowired
    private QueryChildOrgDtoOperation queryChildOrgDtoOperation;

    @Autowired
    private OperateUserPermissionService opUserPermService;

    @Autowired
    private UserCacheService userCacheService;

//    @Autowired
//    private QueryChildOrgModelListOperation queryChildOrgModelListOperation;

    @Autowired
    public OrgUserServiceImpl(OrgUserMapper modelMapper, OrgUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<UserModel> listUsersByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        UserModel selfUser = UserUtils.getLoginUser();
        String selfUserId = null;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();

        List<UserModel> users = getModelMapper().listUsersByOrgId(orgId, selfUserId, excludeRoleIds, excludeUserIds);

        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgIdNoRestrict(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);

        List<UserModel> users = getModelMapper().listUsersByOrgId(orgId, null, null, null);

        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds) {
        log.info("orgId:{}, uname:{}", orgId, uname);

        // 默认查出orgId下所有子组织的用户
        List<String> orgIds = new ArrayList<>();
        // 先添加父级id
        if (StringUtils.isNotBlank(orgId)) {
            orgIds.add(orgId);  // 把自己先加进去
            List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevelExcludeSelf(orgId);
            orgIds.addAll(childOrgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        }
        String likeUname = SQLUtils.escapeLike(uname);
        UserDto userCondition = new UserDto();
        userCondition.setUsername(likeUname);

        UserModel selfUser = UserUtils.getLoginUser();
        String selfUserId = null;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();

        List<UserTmpDto> users = getModelMapper().listUsers(orgIds, userIds, null, userCondition, selfUserId, excludeRoleIds, excludeUserIds);

        List<UserModel> userModels = users.stream().map(u -> {
            UserModel userModel = new UserModel();
            BeanUtils.copyProperties(u, userModel);
            return userModel;
        }).collect(Collectors.toList());

        return userModels;
    }

    @Override
    public List<OrgModel> listOrgsByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
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
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        opUserPermService.checkByTargetUserIds(userIds);

        var objResult = orgService.get(orgId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED);
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

        userCacheService.evictAllUserCache();
        return true;

    }

    @Override
    public String getAllOrgPathByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

        List<OrgModel> orgs = listOrgsByUserId(userId);
//        String allorgpath = orgs.stream().map(o -> orgService.calculatePathByOrgId(o.getId())).collect(Collectors.joining(","));
        String allorgpath = orgs.stream().map(OrgModel::getPath).collect(Collectors.joining(","));
        return allorgpath;
    }

    @Override
    public Integer getUserCnt(String orgId) {
        Integer userCnt = getModelMapper().selectUserCnt(orgId);

        return userCnt;
    }

    @Override
    public PageInfo<UserDto> pageUsersByOrg(OrgUserPageRequest req) {

        List<String> idsList = null;
        if (Objects.nonNull(req.getIds()) && req.getIds().length > 0) {
            idsList = Arrays.asList(req.getIds());
        }

        List<String> orgIds = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getOrg())) {
            // 添加父级org（把自己先加进去）
            orgIds.add(req.getOrg());
            if (req.isShowAllUnderLevel()) {
                List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevelExcludeSelf(req.getOrg());
                if (CollectionUtil.isNotEmpty(childOrgDtos)) {
                    orgIds.addAll(childOrgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
                }
            }
        }

        log.info("orgIds: {}", orgIds);

        ArrayList<String> orders = null;
        if (req.getOrders() != null && req.getOrders().length > 0) {
            orders = CollectionUtil.newArrayList(req.getOrders());
        }

        String likeUsername = SQLUtils.escapeLike(req.getUsername());
        UserDto userCondition = new UserDto();
        userCondition.setUsername(likeUsername);
        userCondition.setIsEnabled(req.getIsEnabled());
        userCondition.setIsAudited(req.getIsAudited());
        userCondition.setIsExpired(req.getIsExpired());

        UserModel selfUser = UserUtils.getLoginUser();
        String selfUserId = null;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();
        List<UserTmpDto> userTmpDtos = getModelMapper().listUsers(orgIds, idsList, orders, userCondition, selfUserId, excludeRoleIds, excludeUserIds);
        PageInfo pageInfo = new PageInfo(userTmpDtos);

        //List<UserDto> userDtos = users.stream().map(u -> {
        //    UserDto userDto = userService.userModel2UserDto(u);
        //    return userDto;
        //}).collect(Collectors.toList());
        List<UserDto> userDtos = processUserTmpDto2UserDto(userTmpDtos);

        pageInfo.setList(userDtos);
        return pageInfo;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

//        var objResult = userService.get(userId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");
//        }

        getModelMapper().deleteByUserId(userId);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean deleteByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);

//        var objResult = orgService.get(orgId).getSource();
//        if (objResult == null || objResult.getObj() == null) {
//            new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
//        }

        getModelMapper().deleteByOrgId(orgId);
        userCacheService.evictAllUserCache();
        return true;
    }


    private List<UserDto> processUserTmpDto2UserDto(List<UserTmpDto> users) {
        List<OrgModel> allOrgs = orgService.listAll(AuthConstant.DEFAULT_TENANT);
        List<RoleModel> allRoles = roleService.listByTenantId(AuthConstant.DEFAULT_TENANT);
        List<UserDto> userDtos = new ArrayList<>();
        for (UserTmpDto userTmpDto : users) {
            UserDto userDto = new UserDto();
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(userTmpDto, userDto);
            // 过期状态处理
            userService.processExpiredStatus(userDto);

            if (StringUtils.isNotBlank(userTmpDto.getOrgs())) {
                List<String> userOrgIds = Arrays.asList(userTmpDto.getOrgs().split(","));
                List<OrgModel> userOrgModels = allOrgs.stream().filter(o -> userOrgIds.contains(o.getId())).collect(Collectors.toList());
                List<List<String>> allorgpath = userOrgModels.stream().map(o -> CollectionUtil.toList(StringUtils.split(o.getPath(), AuthConstant.ORG_PATH_SEPARATOR))).collect(Collectors.toList());
                userDto.setOrgs(userOrgModels);
                userDto.setOrgIds(userOrgIds);
                userDto.setOrgpaths(allorgpath);
            }

            if (StringUtils.isNotBlank(userTmpDto.getRoles())) {
                List<String> userRoleIds = Arrays.asList(userTmpDto.getRoles().split(","));
                List<RoleModel> userRoleModels = allRoles.stream().filter(r -> userRoleIds.contains(r.getId())).collect(Collectors.toList());

                userDto.setRoles(userRoleModels);

                boolean isAdmin = userRoleIds.stream().anyMatch(rid -> Objects.equals(rid, AuthConstant.ROLE_ADMIN_ID));
                boolean isSuperUser = Objects.equals(userTmpDto.getId(), AuthConstant.ROOT_USER_ID);

                if (isAdmin) {
                    userDto.setIsAdmin(1);
                } else {
                    userDto.setIsAdmin(0);
                }

                if (isSuperUser) {
                    userDto.setIsSuperUser(1);
                } else {
                    userDto.setIsSuperUser(0);
                }
            } else {
                userDto.setIsSuperUser(0);
                userDto.setIsAdmin(0);
            }

            userDtos.add(userDto);
        }

        return userDtos;
    }

}
