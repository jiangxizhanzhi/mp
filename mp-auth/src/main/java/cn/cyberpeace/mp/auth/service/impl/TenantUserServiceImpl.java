package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.bo.org.UserOrgBo;
import cn.cyberpeace.mp.auth.bo.org.UserOrgMapBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserOrgRoleBo;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.TenantUserMapper;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.TenantUserFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_MEDIUM)
public class TenantUserServiceImpl
        extends BaseModelServiceImpl<TenantUserModel, TenantUserMapper, TenantUserFields, QueryParams>
        implements TenantUserService {

    @Autowired
    public TenantUserServiceImpl(TenantUserMapper modelMapper, TenantUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    public static final String CACHE_VAL_ORGS_BY_USERID = "ORGS_BY_USERID";

    public static final String CACHE_VAL_ORGS_LIST_ALL = "ORGS_LIST_ALL";

    @Autowired
    private OperateUserPermissionService opUserPermService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private QueryParentOrgDtoOperation queryParentOrgOperation;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private OrgMapper orgMapper;

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
        users.forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgIdNoRestrict(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);

        List<UserModel> users = getModelMapper().listUsersByOrgId(orgId, null, null, null);
        users.forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds) {
        // 默认查出orgId下所有子组织的用户
        List<String> orgIds = new ArrayList<>();
        // 先添加父级id
        if (StringUtils.isNotBlank(orgId)) {
            orgIds.add(orgId);  // 把自己先加进去
            List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevelExcludeSelf(orgId);
            orgIds.addAll(childOrgDtos.stream().map(OrgDto::getId).collect(Collectors.toList()));
        }
        String likeUname = SQLUtils.escapeLike(uname);
        UserBo userCondition = new UserBo();
        userCondition.setUsername(likeUname);

        UserModel selfUser = UserUtils.getLoginUser();
        String selfUserId = null;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<UserOrgRoleBo> users = getModelMapper().listUsers(orgIds, userIds, null, userCondition, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime);

        List<UserModel> userModels = users.stream().map(u -> {
            UserModel userModel = new UserModel();
            BeanUtils.copyProperties(u, userModel);
            userService.setDefaultIfNoAvatar(userModel);
            return userModel;
        }).collect(Collectors.toList());

        return userModels;
    }

    @Override
    @Cacheable(value = CACHE_VAL_ORGS_BY_USERID)
    public List<OrgBO> listOrgsByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        List<OrgModel> orgs = SelectOrgsByUserId(userId);
        List<OrgBO> orgBOS =
                orgs.stream().map(orgModel -> {
                    OrgBO orgBO = new OrgBO();
                    BeanUtils.copyProperties(orgModel, orgBO);
                    orgBO.setPath(orgService.getPath(orgModel));
                    return orgBO;
                }).collect(Collectors.toList());
        return orgBOS;
    }

    @Override
    @Cacheable(value = CACHE_VAL_ORGS_BY_USERID)
    public List<UserOrgMapBo> listUserOrgMapDtosByUserIds(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_ID);
        List<UserOrgMapBo> userOrgMapBos = getModelMapper().listUserOrgMapDtosByUserIds(userIds);
        return userOrgMapBos;
    }

    @Override
    public List<UserOrgBo> listUserOrgByUserIds(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_ID);
        List<UserOrgBo> userOrgBos = getModelMapper().listUserOrgByUserIds(userIds);
        for (UserOrgBo userOrgBo : userOrgBos) {
            String path = orgService.getPath(userOrgBo);
            userOrgBo.setPath(path);
        }
        return userOrgBos;
    }

    @Override
    public List<OrgDto> listHierarchicalOrgsByUserId(String userId) {
        List<OrgModel> orgModels = SelectOrgsByUserId(userId);
        List<OrgBO> orgBOS =
                orgModels.stream().map(orgModel ->
                        orgService.orgModel2OrgBO(orgModel)
                ).collect(Collectors.toList());

        List<OrgDto> orgDtos = new ArrayList<>();
        orgBOS.forEach(o -> {
            OrgDto orgDto = new OrgDto();
            BeanUtils.copyProperties(o, orgDto);
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
        List<TenantUserModel> tenantUserModels = new ArrayList<>();
        HumanCondition condition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId)
                .selectAs(TenantUserModel::getUser, "user");
        Set<String> userIdSet = new HashSet<>();
        List<Map<String, Object>> userMaps = getModelMapper().originList(condition);
        userMaps.forEach(userMap -> {
            String user = (String) userMap.get("user");
            userIdSet.add(user);
        });
        userIds.forEach(uid -> {
            if (!userIdSet.contains(uid)) {
                TenantUserModel orguser = new TenantUserModel();
                orguser.setOrg(orgId);
                orguser.setUser(uid);
                orguser.setTenant(AuthConstant.DEFAULT_TENANT);
                tenantUserModels.add(orguser);
            }
        });
        getModelMapper().bulkCreate(tenantUserModels);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public String getAllOrgPathByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

        List<OrgBO> orgBOS = listOrgsByUserId(userId);
        //String allorgpath = orgService.getBatchPath(orgs);
        String allorgpath = orgBOS.stream().map(OrgBO::getPath).collect(Collectors.joining(","));
        return allorgpath;
    }

    @Override
    public Integer getUserCnt(String orgId) {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId);
        Integer userCnt = getModelMapper().total(humanCondition);
        return userCnt;
    }

    @Override
    public PageInfo<UserBo> pageUsersByOrg(OrgUserPageRequest req) {
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

        ArrayList<String> orders = null;
        if (req.getOrders() != null && req.getOrders().length > 0) {
            orders = CollectionUtil.newArrayList(req.getOrders());
        }

        String likeUsername = SQLUtils.escapeLike(req.getUsername());
        UserBo userCondition = new UserBo();
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
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<UserOrgRoleBo> userOrgRoleBos = getModelMapper().listUsers(orgIds, idsList, orders, userCondition, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime);
        PageInfo pageInfo = new PageInfo(userOrgRoleBos);
        List<UserBo> userBos = userService.userOrgRoleDtos2UserDtos(userOrgRoleBos);

        pageInfo.setList(userBos);
        return pageInfo;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getUser, userId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean deleteByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean deleteByTenantId(String tenantId) {
        // todo
        return true;
    }

    private List<OrgModel> SelectOrgsByUserId(String userId) {
        HumanCondition tenantUserCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getUser, userId)
                .select(TenantUserModel::getOrg);
        List<Map<String, Object>> tenantUserMaps = getModelMapper().originList(tenantUserCondition);
        List<String> ids = new ArrayList<>();
        tenantUserMaps.forEach(map -> {
            ids.add((String) map.get("org"));
        });
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition orgCondition = new HumanCondition()
                .filterIn(OrgModel::getId, ids.toArray())
                .distinct();
        List<OrgModel> list = orgMapper.list(orgCondition);
        return list;
    }
}
