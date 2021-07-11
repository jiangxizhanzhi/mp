package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.bo.org.UserOrgBo;
import cn.cyberpeace.mp.auth.bo.org.UserOrgMapBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserOrgRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.TenantUserMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


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

    @Autowired
    private TenantUserMapper tenantUserMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserMapper userMapper;

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
        List<UserModel> users = getUsersByOrgId(orgId, selfUserId, excludeRoleIds, excludeUserIds);
        users.forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgIdNoRestrict(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        List<UserModel> users = getUsersByOrgId(orgId, null, null, null);
        users.forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    @Override
    public List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds) {
        
        List<String> orgIds = new ArrayList<>();
        
        if (StringUtils.isNotBlank(orgId)) {
            orgIds.add(orgId);  
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
        List<UserOrgRoleBo> users = listAllUsers(orgIds, userIds, null, userCondition, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime);

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
        List<OrgModel> orgs = selectOrgsByUserId(userId);
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

        HumanCondition humanCondition = new HumanCondition()
                .link(OrgModel.class, TenantUserModel.class)
                .filterLink(OrgModel.class, OrgModel::getId, TenantUserModel.class, TenantUserModel::getOrg)
                .currentLink(TenantUserModel.class)
                .filterNotEqual(TenantUserModel::getUser, "");
        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(TenantUserModel::getUser, userIds.toArray());
        }
        humanCondition.currentLink(TenantUserModel.class)
                .selectAs(TenantUserModel::getUser, "userId")
                .currentLink(OrgModel.class)
                .selectAs(GroupConcat.f(OrgModel::getId).distinct(), "orgIds")
                .selectAs(GroupConcat.f(OrgModel::getName).distinct(), "orgNames")
                .currentLink(TenantUserModel.class)
                .groupBy(TenantUserModel::getUser);
        return getModelMapper().list(humanCondition, UserOrgMapBo.class);
    }

    @Override
    public List<UserOrgBo> listUserOrgByUserIds(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_ID);

        HumanCondition humanCondition = new HumanCondition()
                .link(OrgModel.class, TenantUserModel.class)
                .filterLink(OrgModel.class, OrgModel::getId, TenantUserModel.class, TenantUserModel::getOrg)
                .currentLink(TenantUserModel.class)
                .filterNotEqual(TenantUserModel::getUser, "");
        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(TenantUserModel::getUser, userIds.toArray());
        }
        humanCondition.currentLink(OrgModel.class)
                .select(OrgFields.allFields.getFields())
                .distinct()
                .currentLink(TenantUserModel.class)
                .selectAs(TenantUserModel::getUser, "userId");
        List<UserOrgBo> userOrgBos = getModelMapper().list(humanCondition, UserOrgBo.class);
        for (UserOrgBo userOrgBo : userOrgBos) {
            String path = orgService.getPath(userOrgBo);
            userOrgBo.setPath(path);
        }
        return userOrgBos;
    }

    @Override
    public List<OrgDto> listHierarchicalOrgsByUserId(String userId) {
        List<OrgModel> orgModels = selectOrgsByUserId(userId);
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

        

        if (CollectionUtil.isEmpty(userIds)) {
            
            return true;
        }

        
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

        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();
        LocalDateTime currentDateTime = LocalDateTime.now();
        PageInfo pageInfo = listPageUsers(orgIds, idsList, orders, userCondition, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime, req.getPage(), req.getPageSize());

        return pageInfo;
    }


    public List<UserBo> listByUserIds(List<String> ids) {
        UserModel selfUser = UserUtils.getLoginUser();
        String selfUserId = null;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        List<String> excludeRoleIds = opUserPermService.getExcludeRoleIdsByLoginUserForSearchingUsers();
        List<String> excludeUserIds = opUserPermService.getExcludeUserIdsByLoginUserForSearchingUsers();
        LocalDateTime currentDateTime = LocalDateTime.now();

        PageInfo<UserBo> pageInfo = listPageUsers(null, ids, null, null, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime, 1, ids.size());
        return pageInfo.getList();

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
        
        return true;
    }

    private List<OrgModel> selectOrgsByUserId(String userId) {
        HumanCondition tenantUserCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getUser, userId)
                .select(TenantUserModel::getOrg);
        List<Map<String, Object>> tenantUserMaps = getModelMapper().originList(tenantUserCondition);
        List<String> ids = new ArrayList<>();
        tenantUserMaps.forEach(map ->
                ids.add((String) map.get("org"))
        );
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition orgCondition = new HumanCondition()
                .filterIn(OrgModel::getId, ids.toArray())
                .distinct();
        List<OrgModel> list = orgMapper.list(orgCondition);
        return list;
    }

    private List<UserModel> getUsersByOrgId(String orgId, String selfUserId, List<String> excludeRoleIds, List<String> excludeUserIds) {
        HumanCondition humanCondition = new HumanCondition();

        HumanCondition tenantCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId)
                .select(TenantUserModel::getUser);
        List<TenantUserModel> tenantUserModels = tenantUserMapper.list(tenantCondition);
        List<String> tenantUserIds = tenantUserModels.stream().map(TenantUserModel::getUser).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(tenantUserIds)) {
            return new ArrayList<>();
        }
        humanCondition.filterIn(UserModel::getId, tenantUserIds.toArray());
        if (CollectionUtil.isNotEmpty(excludeRoleIds)) {
            HumanCondition userRoleCondition = new HumanCondition()
                    .filterIn(UserRoleModel::getRole, excludeRoleIds.toArray());
            if (StringUtils.isNotBlank(selfUserId)) {
                userRoleCondition.filterNotEqual(UserRoleModel::getUser, selfUserId);
            }
            userRoleCondition.select(UserRoleModel::getUser).distinct();
            List<UserRoleModel> userRoleModels = userRoleMapper.list(userRoleCondition);
            List<String> roleUserIds = userRoleModels.stream().map(UserRoleModel::getUser).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(roleUserIds)) {
                humanCondition.filterNotIn(UserModel::getId, roleUserIds.toArray());
            }
        }

        if (CollectionUtil.isNotEmpty(excludeUserIds)) {
            humanCondition.filterNotIn(UserModel::getId, excludeUserIds.toArray());
        }
        humanCondition.orderBy(UserModel::getUpdateTime, true).orderBy(UserModel::getUsername);
        List<UserModel> userModels = userMapper.list(humanCondition);
        return userModels;
    }

    public List<UserOrgRoleBo> listAllUsers(List<String> orgIds,
                                            List<String> userIds,
                                            List<String> orders,
                                            UserBo userBo,
                                            String selfUserId,
                                            List<String> excludeRoleIds,
                                            List<String> excludeUserIds,
                                            LocalDateTime currentDateTime
    ) {
        HumanCondition humanCondition = genHumanCondition(orgIds, userIds, orders, userBo, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime);
        if (Objects.isNull(humanCondition)) {
            return new ArrayList<>();
        }
        Map<String, String> userOrgMap = getUserOrgMap();
        Map<String, String> userRoleMap = getUserRoleMap();
        List<UserModel> userModels = userMapper.list(humanCondition);
        List<UserOrgRoleBo> userOrgRoleBos = new ArrayList<>(userModels.size());
        for (UserModel model : userModels) {
            UserOrgRoleBo bo = new UserOrgRoleBo();
            BeanUtils.copyProperties(model, bo);
            bo.setOrgs(userOrgMap.get(model.getId()));
            bo.setRoles(userRoleMap.get(model.getId()));
            userOrgRoleBos.add(bo);
        }
        return userOrgRoleBos;
    }

    public PageInfo<UserBo> listPageUsers(List<String> orgIds,
                                         List<String> userIds,
                                         List<String> orders,
                                         UserBo userBo,
                                         String selfUserId,
                                         List<String> excludeRoleIds,
                                         List<String> excludeUserIds,
                                         LocalDateTime currentDateTime,
                                         Integer page,
                                         Integer pageSize
    ) {
        HumanCondition humanCondition = genHumanCondition(orgIds, userIds, orders, userBo, selfUserId, excludeRoleIds, excludeUserIds, currentDateTime);
        if (Objects.isNull(humanCondition)) {
            return new PageInfo<>(new ArrayList<>(), 0);
        }
        int total = userMapper.total(humanCondition);
        Map<String, String> userOrgMap = getUserOrgMap();
        Map<String, String> userRoleMap = getUserRoleMap();

        PageHelper.startPage(page, pageSize);
        List<UserModel> userModels = userMapper.list(humanCondition);
        PageInfo pageInfo = new PageInfo(userModels);
        pageInfo.setTotal(total);
        List<UserOrgRoleBo> userOrgRoleBos = new ArrayList<>(userModels.size());
        for (UserModel model : userModels) {
            UserOrgRoleBo bo = new UserOrgRoleBo();
            BeanUtils.copyProperties(model, bo);
            bo.setOrgs(userOrgMap.get(model.getId()));
            bo.setRoles(userRoleMap.get(model.getId()));
            userOrgRoleBos.add(bo);
        }

        List<UserBo> userBos = userService.userOrgRoleDtos2UserDtos(userOrgRoleBos);
        pageInfo.setList(userBos);
        return pageInfo;
    }

    private HumanCondition genHumanCondition(List<String> orgIds,
                                             List<String> userIds,
                                             List<String> orders,
                                             UserBo userBo,
                                             String selfUserId,
                                             List<String> excludeRoleIds,
                                             List<String> excludeUserIds,
                                             LocalDateTime currentDateTime) {
        HumanCondition humanCondition = new HumanCondition();

        if (CollectionUtil.isNotEmpty(orgIds)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(TenantUserModel::getOrg, orgIds.toArray())
                    .select(TenantUserModel::getUser)
                    .distinct();
            List<TenantUserModel> tenantUserModels = tenantUserMapper.list(orgCondition);
            List<String> ids = tenantUserModels.stream().map(TenantUserModel::getUser).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(ids)) {
                return null;
            }
            humanCondition.filterIn(UserModel::getId, ids.toArray());
        }

        if (CollectionUtil.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserModel::getId, userIds.toArray());
        }

        if (CollectionUtil.isNotEmpty(excludeRoleIds)) {
            HumanCondition excludeRoleCondition = new HumanCondition()
                    .filterIn(UserRoleModel::getRole, excludeRoleIds.toArray());
            if (StringUtils.isNotBlank(selfUserId)) {
                excludeRoleCondition.filterNotEqual(UserRoleModel::getUser, selfUserId);
            }
            excludeRoleCondition.select(UserRoleModel::getUser);
            List<UserRoleModel> userRoleModels = userRoleMapper.list(excludeRoleCondition);
            List<String> ids = userRoleModels.stream().map(UserRoleModel::getUser).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(ids)) {
                humanCondition.filterNotIn(UserModel::getId, ids.toArray());
            }
        }

        if (CollectionUtil.isNotEmpty(excludeUserIds)) {
            humanCondition.filterNotIn(UserModel::getId, excludeUserIds.toArray());
        }

        if (CollectionUtil.isNotEmpty(excludeRoleIds)) {
            humanCondition.filterNotIn(UserModel::getId, excludeRoleIds.toArray());
        }

        if (Objects.nonNull(userBo)) {
            if (Objects.nonNull(userBo.getIsEnabled())) {
                humanCondition.filterEqual(UserModel::getIsEnabled, userBo.getIsEnabled());
            }
            if (Objects.nonNull(userBo.getIsAudited())) {
                humanCondition.filterEqual(UserModel::getIsAudited, userBo.getIsAudited());
            }
            if (Objects.nonNull(userBo.getIsExpired()) && Objects.equals(userBo.getIsExpired(), 0)) {
                humanCondition.filterGreaterThan(UserModel::getExpiredTime, currentDateTime);
            }
            if (Objects.nonNull(userBo.getIsExpired()) && !Objects.equals(userBo.getIsExpired(), 0)) {
                humanCondition.filterLessThan(UserModel::getExpiredTime, currentDateTime);
            }
            if (StringUtils.isNotBlank((userBo.getUsername()))) {
                humanCondition.filterOrStart()
                        .orContains(UserModel::getUsername, userBo.getUsername())
                        .orContains(UserModel::getName, userBo.getUsername())
                        .filterOrEnd();
            }
        }
        humanCondition.groupBy(UserModel::getId);
        if (CollectionUtil.isNotEmpty(orders)) {
            for (String order : orders) {
                humanCondition.orderBy(order);
            }
        }
        return humanCondition;
    }

    private Map<String, String> getUserOrgMap() {
        HumanCondition orgCondition = new HumanCondition()
                .select(TenantUserModel::getUser)
                .selectAs(GroupConcat.f(TenantUserModel::getOrg).distinct(), "orgs")
                .groupBy(TenantUserModel::getUser);
        List<Map<String, Object>> userOrgMaps = tenantUserMapper.originList(orgCondition);
        Map<String, String> userOrgMap = new HashMap<>(userOrgMaps.size());
        userOrgMaps.forEach(map -> {
            String userId = (String) map.get("user");
            String orgs = (String) map.get("orgs");
            userOrgMap.put(userId, orgs);
        });
        return userOrgMap;
    }

    private Map<String, String> getUserRoleMap() {
        HumanCondition roleCondition = new HumanCondition()
                .select(UserRoleModel::getUser)
                .selectAs(GroupConcat.f(UserRoleModel::getRole).distinct(), "roles")
                .groupBy(UserRoleModel::getUser);
        List<Map<String, Object>> userRoleMaps = userRoleMapper.originList(roleCondition);
        Map<String, String> userRoleMap = new HashMap<>(userRoleMaps.size());
        userRoleMaps.forEach(map -> {
            String userId = (String) map.get("user");
            String roles = (String) map.get("roles");
            userRoleMap.put(userId, roles);
        });
        return userRoleMap;
    }
}
