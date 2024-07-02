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
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.auth.service.fields.TenantUserFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.SQLUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * TenantUserServiceImpl
     *
     * @param modelMapper
     * @param modelFields
     */
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

    /**
     * listUsersByOrgId
     *
     * @param orgId
     * @return
     */
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
        userService.setDefaultIfNoAvatars(users);
//        users.forEach(userModel -> userService.setDefaultIfNoAvatar(userModel));
        return users;
    }

    /**
     * listUsersByOrgIdNoRestrict
     *
     * @param orgId
     * @return
     */
    @Override
    public List<UserModel> listUsersByOrgIdNoRestrict(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        List<UserModel> users = getUsersByOrgId(orgId, null, null, null);
        for (UserModel userModel : users) {
            userService.setDefaultIfNoAvatar(userModel);
        }
        return users;
    }

    /**
     * listUsersByOrgAndUname
     *
     * @param orgId
     * @param uname
     * @param userIds
     * @return
     */
    @Override
    public List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds) {
        // 默认查出orgId下所有子组织的用户
        List<String> orgIds = new ArrayList<>();
        // 先添加父级id
        if (StringUtils.isNotBlank(orgId)) {
            orgIds.add(orgId);  // 把自己先加进去
            List<OrgDto> childOrgDtos = orgService.listAllChildIgnoreLevelExcludeSelf(orgId);
            List<String> list = new ArrayList<>();
            for (OrgDto childOrgDto : childOrgDtos) {
                String id = childOrgDto.getId();
                list.add(id);
            }
            orgIds.addAll(list);
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

        List<UserModel> userModels = new ArrayList<>();
        for (UserOrgRoleBo user : users) {
            UserModel userModel = new UserModel();
            BeanUtils.copyProperties(user, userModel);
            userService.setDefaultIfNoAvatar(userModel);
            UserModel apply = userModel;
            userModels.add(apply);
        }

        return userModels;
    }

    /**
     * listOrgsByUserId
     *
     * @param userId
     * @return
     */
    @Override
    @Cacheable(value = CACHE_VAL_ORGS_BY_USERID)
    public List<OrgBO> listOrgsByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        List<OrgModel> orgs = selectOrgsByUserId(userId);
        List<OrgBO> orgBOS =
                new ArrayList<>();
        for (OrgModel org : orgs) {
            OrgBO orgBO = new OrgBO();
            BeanUtils.copyProperties(org, orgBO);
            orgBO.setPath(orgService.getPath(org));
            OrgBO apply = orgBO;
            orgBOS.add(apply);
        }
        return orgBOS;
    }

    /**
     * listUserOrgMapDtosByUserIds
     *
     * @param userIds
     * @return
     */
    @Override
    @Cacheable(value = CACHE_VAL_ORGS_BY_USERID)
    public List<UserOrgMapBo> listUserOrgMapDtosByUserIds(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_ID);

        HumanCondition humanCondition = new HumanCondition()
                .link(OrgModel.class, TenantUserModel.class)
                .filterLink(OrgModel.class, OrgModel::getId, TenantUserModel.class, TenantUserModel::getOrg)
                .currentLink(TenantUserModel.class)
                .filterNotEqual(TenantUserModel::getUser, "");
        if (CollectionUtils.isNotEmpty(userIds)) {
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

    /**
     * listUserOrgByUserIds
     *
     * @param userIds
     * @return
     */
    @Override
    public List<UserOrgBo> listUserOrgByUserIds(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_ID);

        HumanCondition humanCondition = new HumanCondition()
                .link(OrgModel.class, TenantUserModel.class)
                .filterLink(OrgModel.class, OrgModel::getId, TenantUserModel.class, TenantUserModel::getOrg)
                .currentLink(TenantUserModel.class)
                .filterNotEqual(TenantUserModel::getUser, "");
        if (CollectionUtils.isNotEmpty(userIds)) {
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

    /**
     * listHierarchicalOrgsByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public List<OrgDto> listHierarchicalOrgsByUserId(String userId) {
        List<OrgModel> orgModels = selectOrgsByUserId(userId);
        List<OrgBO> orgBOS =
                new ArrayList<>();
        for (OrgModel orgModel : orgModels) {
            OrgBO orgBO = orgService.orgModel2OrgBO(orgModel);
            orgBOS.add(orgBO);
        }

        List<OrgDto> orgDtos = new ArrayList<>();
        for (OrgBO o : orgBOS) {
            OrgDto orgDto = new OrgDto();
            BeanUtils.copyProperties(o, orgDto);
            orgDto.execute(queryParentOrgOperation);
            orgDtos.add(orgDto);
        }

        return orgDtos;
    }

    /**
     * additionalBindOrgUsers
     *
     * @param orgId
     * @param userIds
     * @return boolean
     */
    @Override
    public boolean additionalBindOrgUsers(String orgId, List<String> userIds) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        opUserPermService.checkByTargetUserIds(userIds);

        ObjResult<OrgModel> objResult = orgService.get(orgId).getSource();
        if (objResult == null || objResult.getObj() == null) {
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED);
        }

        // 不清楚组织-用户关系，而是增量添加

        if (CollectionUtils.isEmpty(userIds)) {
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
        for (Map<String, Object> userMap : userMaps) {
            String user = (String) userMap.get("user");
            userIdSet.add(user);
        }
        for (String uid : userIds) {
            if (!userIdSet.contains(uid)) {
                TenantUserModel orguser = new TenantUserModel();
                orguser.setOrg(orgId);
                orguser.setUser(uid);
                orguser.setTenant(AuthConstant.DEFAULT_TENANT);
                tenantUserModels.add(orguser);
            }
        }
        getModelMapper().bulkCreate(tenantUserModels);
        userCacheService.evictAllUserCache();
        return true;
    }

    /**
     * getAllOrgPathByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public String getAllOrgPathByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);

        List<OrgBO> orgBOS = listOrgsByUserId(userId);
        //String allorgpath = orgService.getBatchPath(orgs);
        StringJoiner joiner = new StringJoiner(",");
        for (OrgBO orgBO : orgBOS) {
            String path = orgBO.getPath();
            joiner.add(path);
        }
        String allorgpath = joiner.toString();
        return allorgpath;
    }

    /**
     * getUserCnt
     *
     * @param orgId
     * @return
     */
    @Override
    public Integer getUserCnt(String orgId) {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId);
        Integer userCnt = getModelMapper().total(humanCondition);
        return userCnt;
    }

    /**
     * pageUsersByOrg
     *
     * @param req
     * @return
     */
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
                if (CollectionUtils.isNotEmpty(childOrgDtos)) {
                    List<String> list = new ArrayList<>();
                    for (OrgDto childOrgDto : childOrgDtos) {
                        String id = childOrgDto.getId();
                        list.add(id);
                    }
                    orgIds.addAll(list);
                }
            }
        }

        ArrayList<String> orders = null;
        if (req.getOrders() != null && req.getOrders().length > 0) {
            orders = new ArrayList<>(Arrays.asList(req.getOrders()));
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


    /**
     * listByUserIds
     *
     * @param ids
     * @return
     */
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

    /**
     * deleteByUserId
     *
     * @param userId
     * @return boolean
     */
    @Override
    public boolean deleteByUserId(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getUser, userId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        return true;
    }

    /**
     * deleteByOrgId
     *
     * @param orgId
     * @return boolean
     */
    @Override
    public boolean deleteByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId);
        getModelMapper().filterDelete(humanCondition);
        userCacheService.evictAllUserCache();
        return true;
    }

    /**
     * selectOrgsByUserId
     *
     * @param userId
     * @return
     */
    private List<OrgModel> selectOrgsByUserId(String userId) {
        HumanCondition tenantUserCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getUser, userId)
                .select(TenantUserModel::getOrg);
        List<Map<String, Object>> tenantUserMaps = getModelMapper().originList(tenantUserCondition);
        List<String> ids = new ArrayList<>();
        for (Map<String, Object> map : tenantUserMaps) {
            ids.add((String) map.get("org"));
        }
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition orgCondition = new HumanCondition()
                .filterIn(OrgModel::getId, ids.toArray())
                .distinct();
        List<OrgModel> list = orgMapper.list(orgCondition);
        return list;
    }

    /**
     * getUsersByOrgId
     *
     * @param orgId
     * @param selfUserId
     * @param excludeRoleIds
     * @param excludeUserIds
     * @return
     */
    private List<UserModel> getUsersByOrgId(String orgId, String selfUserId, List<String> excludeRoleIds, List<String> excludeUserIds) {
        HumanCondition humanCondition = new HumanCondition();

        HumanCondition tenantCondition = new HumanCondition()
                .filterEqual(TenantUserModel::getOrg, orgId)
                .select(TenantUserModel::getUser);
        List<TenantUserModel> tenantUserModels = tenantUserMapper.list(tenantCondition);
        List<String> tenantUserIds = new ArrayList<>();
        for (TenantUserModel tenantUserModel : tenantUserModels) {
            String tenantUserModelUser = tenantUserModel.getUser();
            tenantUserIds.add(tenantUserModelUser);
        }
        if (CollectionUtils.isEmpty(tenantUserIds)) {
            return new ArrayList<>();
        }
        humanCondition.filterIn(UserModel::getId, tenantUserIds.toArray());
        if (CollectionUtils.isNotEmpty(excludeRoleIds)) {
            HumanCondition userRoleCondition = new HumanCondition()
                    .filterIn(UserRoleModel::getRole, excludeRoleIds.toArray());
            if (StringUtils.isNotBlank(selfUserId)) {
                userRoleCondition.filterNotEqual(UserRoleModel::getUser, selfUserId);
            }
            userRoleCondition.select(UserRoleModel::getUser).distinct();
            List<UserRoleModel> userRoleModels = userRoleMapper.list(userRoleCondition);
            List<String> roleUserIds = new ArrayList<>();
            for (UserRoleModel userRoleModel : userRoleModels) {
                String user = userRoleModel.getUser();
                roleUserIds.add(user);
            }
            if (CollectionUtils.isNotEmpty(roleUserIds)) {
                humanCondition.filterNotIn(UserModel::getId, roleUserIds.toArray());
            }
        }

        if (CollectionUtils.isNotEmpty(excludeUserIds)) {
            humanCondition.filterNotIn(UserModel::getId, excludeUserIds.toArray());
        }
        humanCondition.orderBy(UserModel::getUpdateTime, true).orderBy(UserModel::getUsername);
        List<UserModel> userModels = userMapper.list(humanCondition);
        return userModels;
    }

    /**
     * listAllUsers
     *
     * @param orgIds
     * @param userIds
     * @param orders
     * @param userBo
     * @param selfUserId
     * @param excludeRoleIds
     * @param excludeUserIds
     * @param currentDateTime
     * @return
     */
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
        List<UserModel> userModels = userMapper.list(humanCondition);
        // 使用查询有效返回的用户模型，关联获取组织和角色信息，而非全部获取后再拼装
        List<String> userModelIds = new ArrayList<>();
        for (UserModel userModel : userModels) {
            String id = userModel.getId();
            userModelIds.add(id);
        }
        Map<String, String> userOrgMap = getUserOrgMap(userModelIds);
        Map<String, String> userRoleMap = getUserRoleMap(userModelIds);
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

    /**
     * listPageUsers
     *
     * @param orgIds
     * @param userIds
     * @param orders
     * @param userBo
     * @param selfUserId
     * @param excludeRoleIds
     * @param excludeUserIds
     * @param currentDateTime
     * @param page
     * @param pageSize
     * @return
     */
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

        PageHelper.startPage(page, pageSize);
        List<UserModel> userModels = userMapper.list(humanCondition);
        // 使用查询有效返回的用户模型，关联获取组织和角色信息，而非全部获取后再拼装
        List<String> userModelIds = new ArrayList<>();
        for (UserModel userModel : userModels) {
            String id = userModel.getId();
            userModelIds.add(id);
        }
        Map<String, String> userOrgMap = getUserOrgMap(userModelIds);
        Map<String, String> userRoleMap = getUserRoleMap(userModelIds);

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

    /**
     * genHumanCondition
     *
     * @param orgIds
     * @param userIds
     * @param orders
     * @param userBo
     * @param selfUserId
     * @param excludeRoleIds
     * @param excludeUserIds
     * @param currentDateTime
     * @return 查询条件构造器
     */
    private HumanCondition genHumanCondition(List<String> orgIds,
                                             List<String> userIds,
                                             List<String> orders,
                                             UserBo userBo,
                                             String selfUserId,
                                             List<String> excludeRoleIds,
                                             List<String> excludeUserIds,
                                             LocalDateTime currentDateTime) {
        HumanCondition humanCondition = new HumanCondition();

        if (CollectionUtils.isNotEmpty(orgIds)) {
            HumanCondition orgCondition = new HumanCondition()
                    .filterIn(TenantUserModel::getOrg, orgIds.toArray())
                    .select(TenantUserModel::getUser)
                    .distinct();
            List<TenantUserModel> tenantUserModels = tenantUserMapper.list(orgCondition);
            List<String> ids = new ArrayList<>();
            for (TenantUserModel tenantUserModel : tenantUserModels) {
                String user = tenantUserModel.getUser();
                ids.add(user);
            }
            if (CollectionUtils.isEmpty(ids)) {
                return null;
            }
            humanCondition.filterIn(UserModel::getId, ids.toArray());
        }

        if (CollectionUtils.isNotEmpty(userIds)) {
            humanCondition.filterIn(UserModel::getId, userIds.toArray());
        }

        if (CollectionUtils.isNotEmpty(excludeRoleIds)) {
            HumanCondition excludeRoleCondition = new HumanCondition()
                    .filterIn(UserRoleModel::getRole, excludeRoleIds.toArray());
            if (StringUtils.isNotBlank(selfUserId)) {
                excludeRoleCondition.filterNotEqual(UserRoleModel::getUser, selfUserId);
            }
            excludeRoleCondition.select(UserRoleModel::getUser);
            List<UserRoleModel> userRoleModels = userRoleMapper.list(excludeRoleCondition);
            List<String> ids = new ArrayList<>();
            for (UserRoleModel userRoleModel : userRoleModels) {
                String user = userRoleModel.getUser();
                ids.add(user);
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                humanCondition.filterNotIn(UserModel::getId, ids.toArray());
            }
        }

        if (CollectionUtils.isNotEmpty(excludeUserIds)) {
            humanCondition.filterNotIn(UserModel::getId, excludeUserIds.toArray());
        }

        if (CollectionUtils.isNotEmpty(excludeRoleIds)) {
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
        if (CollectionUtils.isNotEmpty(orders)) {
            for (String order : orders) {
                humanCondition.orderBy(order);
            }
        }
        return humanCondition;
    }

    /**
     * getUserOrgMap
     *
     * @param userIdList
     * @return
     */
    private Map<String, String> getUserOrgMap(List<String> userIdList) {
        HumanCondition orgCondition = new HumanCondition()
                .select(TenantUserModel::getUser)
                .selectAs(GroupConcat.f(TenantUserModel::getOrg).distinct(), "orgs");
        if (CollectionUtils.isNotEmpty(userIdList)) {
            orgCondition.filterIn(TenantUserModel::getUser, userIdList.toArray());
        }
        orgCondition.groupBy(TenantUserModel::getUser);
        List<Map<String, Object>> userOrgMaps = tenantUserMapper.originList(orgCondition);
        Map<String, String> userOrgMap = new HashMap<>(userOrgMaps.size());
        for (Map<String, Object> map : userOrgMaps) {
            String userId = (String) map.get("user");
            String orgs = (String) map.get("orgs");
            userOrgMap.put(userId, orgs);
        }
        return userOrgMap;
    }

    /**
     * getUserRoleMap
     *
     * @param userIdList
     * @return
     */
    private Map<String, String> getUserRoleMap(List<String> userIdList) {
        HumanCondition roleCondition = new HumanCondition()
                .select(UserRoleModel::getUser)
                .selectAs(GroupConcat.f(UserRoleModel::getRole).distinct(), "roles");
        if (CollectionUtils.isNotEmpty(userIdList)) {
            roleCondition.filterIn(UserRoleModel::getUser, userIdList.toArray());
        }
        roleCondition.groupBy(TenantUserModel::getUser);
        List<Map<String, Object>> userRoleMaps = userRoleMapper.originList(roleCondition);
        Map<String, String> userRoleMap = new HashMap<>(userRoleMaps.size());
        for (Map<String, Object> map : userRoleMaps) {
            String userId = (String) map.get("user");
            String roles = (String) map.get("roles");
            userRoleMap.put(userId, roles);
        }
        return userRoleMap;
    }
}
