package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ExportUserEn;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.org.OrgBO;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserOrgRoleDto;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.OrgUserMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.*;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Aggregation;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields, UserQueryParams>
        implements UserService {

    public static final String CACHE_VAL_USERMODEL_BY_ID = "USERMODEL_BY_ID_CACHE";

    public static final String CACHE_VAL_USERMODEL_BY_USERNAME = "USERMODEL_BY_USERNAME_CACHE";

    public static final String CACHE_VAL_USERDTO = "USERDTO_CACHE";

    public static final String CACHE_VAL_ORGDTOS_TO_USERDTOS = "ORGDTOS_TO_USERDTOS_CACHE";

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserCreateCheckService createCheckService;

    @Autowired
    private UserUpdateCheckService updateCheckService;

    @Autowired
    private OperateUserPermissionService opUserPermCheckService;

    @Autowired
    private UserImportService userImportService;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private OrgUserMapper orgUserMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    @Cacheable(value = CACHE_VAL_USERMODEL_BY_ID)
    public ObjServiceResult<UserModel> get(String id) {
        ObjServiceResult<UserModel> userModelObjServiceResult = super.get(id);
        UserModel userModel = userModelObjServiceResult.sourceObj();
        setDefaultIfNoAvatar(userModel);
        return userModelObjServiceResult;
    }

    @Override
    @Cacheable(value = CACHE_VAL_USERDTO)
    public UserDto getUserDto(String id) {
        opUserPermCheckService.checkByTargetUserIdsForSearching(Arrays.asList(id));
        UserModel userModel = getModelMapper().get(id);
        setDefaultIfNoAvatar(userModel);
        UserDto userDto = userModel2UserDto(userModel);
        return userDto;
    }

    @Override
    public UserDto getUserDtoNoRestrict(String id) {
        UserModel userModel = getModelMapper().get(id);
        setDefaultIfNoAvatar(userModel);
        UserDto userDto = userModel2UserDto(userModel);
        return userDto;
    }

    /**
     * 判断是否有头像，没有则设置为默认头像
     * @param userModel
     */
    public void setDefaultIfNoAvatar(UserModel userModel) {
        if (Objects.nonNull(userModel)) {
            if (StringUtils.isEmpty(userModel.getAvatar())) {
                userModel.setAvatar(getDefaultUserAvatar());
            }

            userModel.setAvatar(DefaultImgUtil.pathToFdfsUrl(userModel.getAvatar()));
        }
    }

    /**
     * 批量转换头像路径为fdfs地址
     * @param userDtos
     */
    public void batchSetDefaultIfNoAvatar(List<UserDto> userDtos) {
        if (CollectionUtils.isNotEmpty(userDtos)) {
            List<LocalFileMappingModel> mappingModelList =
                    DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.AVATAR.getGroup()));
            String defaultAvatar = getDefaultUserAvatar();
            userDtos.forEach(userDto -> {
                if (StringUtils.isEmpty(userDto.getAvatar())) {
                    userDto.setAvatar(defaultAvatar);
                }
                String path = DefaultImgUtil.pickPathToFdfsUrl(userDto.getAvatar(), mappingModelList);
                userDto.setAvatar(path);
            });
        }
    }


    @Override
    public UserDto getCurrentUserDto() {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.isNull(userModel)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
        }
        setDefaultIfNoAvatar(userModel);
        UserDto userDto = userModel2UserDto(userModel);
        return userDto;
    }

    @Override
    public ListObjServiceResult list(UserQueryParams queryParams) {
        opUserPermCheckService.checkByTargetUserIdsForSearching(Objects.nonNull(queryParams.getIds()) ? Arrays.asList(queryParams.getIds()) : null);
        ListObjServiceResult<UserModel> listObjSrvResult = super.list(queryParams);

        if (Objects.isNull(listObjSrvResult.getSource()) || CollectionUtil.isEmpty(listObjSrvResult.getSource().getObjs())) {
            return listObjSrvResult;
        }

        List<UserModel> users = listObjSrvResult.getSource().getObjs();
        users.stream().forEach(userModel -> setDefaultIfNoAvatar(userModel));
        if (CollectionUtil.isNotEmpty(users)) {
            // 转换
            ListObjServiceResult<UserModel> result = new ListObjServiceResult<>();
            BeanUtils.copyProperties(listObjSrvResult, result);
            result.getSource().setObjs(users);
            return result;
        }

        return listObjSrvResult;
    }

    @Override
    public ObjServiceResult<UserModel> create(UserModel obj, String[] fields) {
        obj.encodePassword();

        return super.create(obj, fields);
    }

    @Override
    public ObjServiceResult<UserModel> update(UserModel obj, String[] fields) {
        obj.encodePassword();
        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }
        obj.setUpdateTime(LocalDateTime.now());
        var ret = super.update(obj, fields);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    public ServiceResult<Integer> bulkCreate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);

        var ret = super.bulkCreate(objs);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    public ServiceResult<Integer> bulkCreateWithPasswordPreSet(List<UserModel> objs) {
        var ret = super.bulkCreate(objs);
        return ret;
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);
        var ret = super.bulkUpdate(objs);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean>  delete(String userId) {
        if (opUserPermCheckService.isOperateSelf(Collections.singletonList(userId))) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_DELETE_SELF);
        }

        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));
        deleteReleated(userId);

        var ret = super.delete(userId);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] userIds) {
        if (opUserPermCheckService.isOperateSelf(Arrays.asList(userIds))) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_DELETE_SELF);
        }

        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userIds));
        for (String userId : userIds) {
            deleteReleated(userId);
        }

        var ret = super.bulkDelete(userIds);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel add(UserRequest createRequest) {
        ParamCheck.notNull(createRequest, AuthBasei18nCode.REQUEST_PARAMS);
        opUserPermCheckService.checkByTargetRoleIds(createRequest.getRoleIds());

        // 保存用户
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(createRequest, userModel);

        String decryptedPwd = PasswordUtils.desDecrypt(createRequest.getPassword());
        userModel.setPassword(decryptedPwd);
        userModel.setTokenSecret(null);
        userModel.setId(null);
        if (StringUtils.isBlank(userModel.getIdentifier())) {
            // 如果ident为空，设置为null
            userModel.setIdentifier(null);
        }
        if (StringUtils.isNotBlank(userModel.getAvatar())) {
            userModel.setAvatar(DefaultImgUtil.fdfsUrlToPath(userModel.getAvatar()));
        }

        createCheckService.check(userModel);

        // create中会对密码加密，外层不需要再次加密
        ObjServiceResult<UserModel> result = create(userModel);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.USER_ADD_ERROR);

        UserModel userCreated = result.getSource().getObj();
        String userId = userCreated.getId();
        log.info("user create success id:{}", userId);

        // 保存组织-用户关系
        if (CollectionUtil.isNotEmpty(createRequest.getOrgIds())) {
            createRequest.getOrgIds().forEach(id -> {
                boolean isOrgExist = orgService.exists(id);
                if (!isOrgExist) {
                    log.error("org id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(id));
                }
                OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(id).build();
                var re = orgUserService.create(orgUserModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ORG_ADD_ERROR);
            });
        }

        // 保存角色-用户关系
        if (CollectionUtil.isNotEmpty(createRequest.getRoleIds())) {
            createRequest.getRoleIds().forEach(id -> {
                boolean isRoleExist = roleService.exists(id);
                if (!isRoleExist) {
                    log.error("role id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.ROLE_NOT_EXISTED.format(id));
                }
                UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(id).build();
                var re = userRoleService.create(userRoleModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ROLE_ADD_ERROR);
            });
        }

        userCacheService.evictAllUserCache();
        return userModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel modify(UserRequest modifyRequest) {
        if (!opUserPermCheckService.isOperateSelf(Arrays.asList(modifyRequest.getId()))) {
            // 如果操作的对象不是自己，则进行权限校验
            opUserPermCheckService.checkByTargetRoleIds(modifyRequest.getRoleIds());
        }

        // 保存用户
        UserModel userModel = new UserModel();
        org.springframework.beans.BeanUtils.copyProperties(modifyRequest, userModel);

        if (StringUtils.isNotBlank(modifyRequest.getPassword())) {
            String decryptedPwd = PasswordUtils.desDecrypt(modifyRequest.getPassword());
            userModel.setPassword(decryptedPwd);
        } else {
            userModel.setPassword(null);
        }
        userModel.setTokenSecret(null);

        updateCheckService.check(userModel);
        if (StringUtils.isNotBlank(userModel.getAvatar())) {
            userModel.setAvatar(DefaultImgUtil.fdfsUrlToPath(userModel.getAvatar()));
        }
        if (StringUtils.isBlank(userModel.getIdentifier())) {
            // 若identifier为空，则设置identifier为null @See http://10.10.10.10/biz/bug-view-2390.html
            userModel.setIdentifier("");
        }
        // udpate中会对密码加密，外层不需要再次加密
        ObjServiceResult<UserModel> result = update(userModel);

        if (Objects.isNull(result.getSource()) || Objects.isNull(result.getSource().getObj())) {
            log.error("update user failed！");
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }

        UserModel userModified = result.getSource().getObj();
        String userId = userModified.getId();
        log.info("user has been modified id:{}", userId);


        // 保存组织-用户关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getOrgIds())) {
            // 清除原来所有用户关联的组织
            orgUserService.deleteByUserId(userId);

            modifyRequest.getOrgIds().forEach(id -> {
                boolean isOrgExist = orgService.exists(id);
                if (!isOrgExist) {
                    log.error("org id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(id));
                }
                OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(id).build();
                var re = orgUserService.create(orgUserModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ORG_ADD_ERROR);
            });
        }


        // 保存角色-用户关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getRoleIds())) {
            // 清除原来所有用户关联的角色
            userRoleService.deleteByUserId(userId);

            modifyRequest.getRoleIds().forEach(id -> {
                boolean isRoleExist = roleService.exists(id);
                if (!isRoleExist) {
                    log.error("role id:{} not existed!", id);
                    throw new MpRuntimeException(AuthErrorCode.ROLE_NOT_EXISTED.format(id));
                }
                UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(id).build();
                var re = userRoleService.create(userRoleModel);
                ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ROLE_ADD_ERROR);
            });
        }

        userCacheService.evictAllUserCache();
        return userModel;
    }

    @Override
    @Cacheable(value = CACHE_VAL_USERMODEL_BY_USERNAME)
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("username", userName));
        setDefaultIfNoAvatar(userModel);
        return userModel;
    }


    @Override
    public List<UserModel> listByUserName(List<String> userNames) {
        HumanCondition humanCondition = new HumanCondition()
                .filterIn(UserModel::getUsername, userNames.toArray());
        List<UserModel> userModels = getModelMapper().list(humanCondition);
        userModels.stream().forEach(userModel -> setDefaultIfNoAvatar(userModel));
        return userModels;
    }

    @Override
    public UserModel getByIdent(String ident) {
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("identifier", ident));
        setDefaultIfNoAvatar(userModel);
        return userModel;
    }

    @Override
    public UserModel getByMobile(String mobile) {
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
        setDefaultIfNoAvatar(userModel);
        return userModel;
    }

    @Override
    public boolean resetSelfPwd(String oldPwd, String newPwd) {
        var currentUser = UserUtils.getLoginUser();
        if (Objects.isNull(currentUser)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
        }

        // 旧密码是否正确
        boolean isOldPwdMatch = PasswordUtils.getPasswordEncoder().matches(oldPwd, currentUser.getPassword());
        if (!isOldPwdMatch) {
            log.error("old password not match！");
            throw new MpRuntimeException(AuthErrorCode.INCORRECT_OLD_CREDENTIALS);
        }

        // 检查新密码强度是否合法
        if (PasswordUtils.isPasswordStrengthLowLevel(newPwd)) {
            throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
        }

        UserModel userUpdate = UserModel.builder().id(currentUser.getId()).password(newPwd).initCheck(UserModel.INT_CHECK_NO).build();
        update(userUpdate);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean resetPwd(String userId, String newPwd) {
        // 密码是否合法
        if (PasswordUtils.isPasswordStrengthLowLevel(newPwd)) {
            throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
        }

        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

        var source = get(userId).getSource();
        UserModel user = Objects.isNull(source) ? null : source.getObj();
        if (Objects.isNull(user)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
        }

        UserModel userUpdate = UserModel.builder().id(userId).password(newPwd).initCheck(UserModel.INT_CHECK_NO).build();
        update(userUpdate);
        userCacheService.evictAllUserCache();
        return true;
    }

    @Override
    public boolean disable(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        if (opUserPermCheckService.isOperateSelf(Collections.singletonList(userId))) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_DISABLE_SELF);
        }
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsEnabled(), UserModel.Status.ENABLED)) {
                UserModel updateUser = UserModel.builder().id(userId).isEnabled(UserModel.Status.DISABLED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("user id:{} has an incorrect status! [Is Not Enabled!]", userId);
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_STATUS_ERROR);
        } else {
            log.error("user id:{} not existed!", userId);
        }
        userCacheService.evictAllUserCache();
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchDisable(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        if (opUserPermCheckService.isOperateSelf(userIds)) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_DISABLE_SELF);
        }
        opUserPermCheckService.checkByTargetUserIds(userIds);

        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                disable(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }
        userCacheService.evictAllUserCache();
        return errMsg.toString();
    }

    @Override
    public boolean enable(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        if (opUserPermCheckService.isOperateSelf(Arrays.asList(userId))) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_OPERATE_SELF);
        }
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsEnabled(), UserModel.Status.DISABLED)) {
                UserModel updateUser = UserModel.builder().id(userId).isEnabled(UserModel.Status.ENABLED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("user id:{} has an incorrect status! [Is Not Disabled!]", userId);
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_STATUS_ERROR);
        } else {
            log.error("user id:{} not existed!", userId);
        }
        userCacheService.evictAllUserCache();
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchEnable(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        if (opUserPermCheckService.isOperateSelf(userIds)) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_OPERATE_SELF);
        }
        opUserPermCheckService.checkByTargetUserIds(userIds);

        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                enable(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }
        userCacheService.evictAllUserCache();
        return errMsg.toString();
    }


    @Override
    public boolean approve(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        if (opUserPermCheckService.isOperateSelf(Arrays.asList(userId))) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_OPERATE_SELF);
        }
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsAudited(), UserModel.Status.NOT_AUDITED)) {
                UserModel updateUser = UserModel.builder().id(userId).isAudited(UserModel.Status.AUDITED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("user id:{} has an incorrect status! [Is Not NOT_AUDITED!]", userId);
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_STATUS_ERROR);
        } else {
            log.error("user id:{} not existed!", userId);
        }
        userCacheService.evictAllUserCache();
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchApprove(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        if (opUserPermCheckService.isOperateSelf(userIds)) {
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_OPERATE_SELF);
        }
        opUserPermCheckService.checkByTargetUserIds(userIds);

        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                approve(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }
        userCacheService.evictAllUserCache();
        return errMsg.toString();
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, UserQueryParams queryParams) {
        String tenant = queryParams.getTenant();
        if (tenant != null) {
            humanCondition.filterEqual("tenant", tenant);
        }

        String username = queryParams.getUsername();
        if (username != null) {
            humanCondition.filterContains("username", username);
        }

        String name = queryParams.getName();
        if (name != null) {
            humanCondition.filterContains("name", name);
        }

        Integer status = queryParams.getStatus();
        if (Objects.nonNull(status)) {
            humanCondition.filterEqual("status", status);
        }
    }


    @Override
    public void downloadExcelTemplate() {
        try {
            HttpServletResponse response = ServletUtils.getResponse();

            Locale locale = LocaleContextHolder.getLocale();

            String res = "excel/import_template.xlsx";
            String fileName = "用户列表.xlsx";

            if (Objects.equals(locale, Locale.US)) {
                res = "excel/import_template_en.xlsx";
                fileName = "UserList.xlsx";
            }

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(res);

            InputStream fis = new BufferedInputStream(is);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();

        } catch (IOException e) {
            log.error("{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(AuthErrorCode.EXCEL_TEMPLATE_DOWNLOAD_ERROR);
        }
    }

    @Override
    public void importUsers(List<ImportUser> list) {
        List<UserModel> userModels = new ArrayList<>();
        List<UserRoleModel> userRoleModels = new ArrayList<>();
        List<OrgUserModel> orgUserModels = new ArrayList<>();
        // 初始化默认头像信息
        for (ImportUser importUser : list) {
            log.info("importUser:{}", importUser);
            // 1.保存用户信息
            UserModel userModel = importUser2UserModel(importUser);
            if (Objects.isNull(userModel)) {
                continue;
            }

            String userId = TextUtils.uuid();
            userModel.setId(userId);
            userModels.add(userModel);

            // http://10.10.10.10/biz/bug-view-4861.html
            // 2. 保存用户-角色关系 之前的步骤已经处理过roleIdList了
            if (CollectionUtil.isNotEmpty(importUser.getRoleIdList())) {
                List<UserRoleModel> userRoles = importUser.getRoleIdList().stream().map(rid -> UserRoleModel.builder().user(userId).role(rid).build()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(userRoles)) {
                    userRoleModels.addAll(userRoles);
                }
            }

            // 3. 保存组织-用户关系
            // http://10.10.10.10/biz/bug-view-4861.html
            if (CollectionUtil.isNotEmpty(importUser.getOrgIdList())) {
                importUser.getOrgIdList().forEach(oid -> {
                    OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(oid).build();
                            var re = orgUserService.create(orgUserModel);
                            ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ORG_ADD_ERROR);
                        }
                );
                List<OrgUserModel> orgUsers = importUser.getOrgIdList().stream().map(oid -> OrgUserModel.builder().user(userId).org(oid).build()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(orgUsers)) {
                    orgUserModels.addAll(orgUsers);
                }
            }
        }

        userImportService.doImport(userModels, userRoleModels, orgUserModels);
    }

    @Override
    public List<ExportUser> exportExcel(List<UserModel> users) {
        List<ExportUser> exportUserList = users.stream().map(this::user2ExportUser).filter(Objects::nonNull).collect(Collectors.toList());
        return exportUserList;
    }

    @Override
    public List<ExportUserEn> exportExcelEn(List<UserModel> users) {
        List<ExportUserEn> exportUserList = users.stream().map(this::user2ExportUserEn).filter(Objects::nonNull).collect(Collectors.toList());
        return exportUserList;
    }

    @Override
    public boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            // 上传服务器
            String filepath = null;
            try {
                String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
                filepath = FdfsCommon.upload(multipartFile.getInputStream(), suffix);
                log.info("uploaded avatar success! filepath:{}", filepath);
            } catch (MpRuntimeException | IOException e) {
                log.error("uploaded error! {}", ExceptionUtil.stacktraceToString(e));
                throw new MpRuntimeException(AuthErrorCode.FILE_ISNULL);
            }

            if (StringUtils.isNotBlank(filepath)) {
                String httpUrl = FdfsCommon.genHttpUrl(filepath);
                UserModel updateUser = UserModel.builder().id(userId).avatar(httpUrl).build();
                var ret = getModelMapper().update(updateUser);
                userCacheService.evictAllUserCache();
                return ret;
            } else {
                log.error("upload failed! userId:{}", userId);
                throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
            }

        } else {
            log.error("user id:{} not existed!", userId);
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
        }

    }


    /**
     * 处理用户过期状态，只有当用户状态为正常状态，且过期时间已到的时候，才会更改为过期状态
     *
     * @param user
     */
    @Override
    public void processExpiredStatus(UserDto user) {
        if (Objects.isNull(user) || Objects.isNull(user.getExpiredTime())) {
            return;
        }

        user.setIsExpired(UserModel.Status.NOT_EXPIRED);

        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            user.setIsExpired(UserModel.Status.EXPIRED);
        }
    }


    @Override
    public UserDto userModel2UserDto(UserModel userModel) {
        if (Objects.isNull(userModel)) {
            return null;
        }
        return doModel2Dto(userModel, true, true);
    }


    @Override
    public UserDto userModel2UserDtoWithoutOrg(UserModel userModel) {
        if (Objects.isNull(userModel)) {
            return null;
        }
        return doModel2Dto(userModel, false, true);
    }

    @Override
    @Cacheable(value = CACHE_VAL_ORGDTOS_TO_USERDTOS)
    public List<UserDto> userOrgRoleDtos2UserDtos(List<UserOrgRoleDto> userOrgRoleDtos) {
        if (CollectionUtil.isEmpty(userOrgRoleDtos)) {
            return Collections.emptyList();
        }
        List<OrgBO> allOrgs = orgService.listAll(AuthConstant.DEFAULT_TENANT);
        List<RoleModel> allRoles = roleService.listByTenantId(AuthConstant.DEFAULT_TENANT);
        List<UserDto> userDtos = new ArrayList<>();
        for (UserOrgRoleDto userOrgRoleDto : userOrgRoleDtos) {
            UserDto userDto = new UserDto();
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(userOrgRoleDto, userDto);
            // 过期状态处理
            processExpiredStatus(userDto);

            if (StringUtils.isNotBlank(userOrgRoleDto.getOrgs())) {
                List<String> userOrgIds = Arrays.asList(userOrgRoleDto.getOrgs().split(","));
                List<OrgBO> userOrgModels = allOrgs.stream().filter(o -> userOrgIds.contains(o.getId())).collect(Collectors.toList());
                //List<List<String>> allorgpath = orgService.getBathPathToList(userOrgModels);
                List<List<String>> allorgpath = userOrgModels.stream().map(o -> CollectionUtil.toList(StringUtils.split(o.getPath(), AuthConstant.ORG_PATH_SEPARATOR))).collect(Collectors.toList());
                userDto.setOrgs(userOrgModels);
                userDto.setOrgIds(userOrgIds);
                userDto.setOrgpaths(allorgpath);
            } else {
                userDto.setOrgs(Collections.emptyList());
                userDto.setOrgIds(Collections.emptyList());
                userDto.setOrgpaths(Collections.emptyList());
            }

            if (StringUtils.isNotBlank(userOrgRoleDto.getRoles())) {
                List<String> userRoleIds = Arrays.asList(userOrgRoleDto.getRoles().split(","));
                List<RoleModel> userRoleModels = allRoles.stream().filter(r -> userRoleIds.contains(r.getId())).collect(Collectors.toList());
                userDto.setRoles(userRoleModels);

                boolean isAdmin = userRoleIds.stream().anyMatch(rid -> Objects.equals(rid, AuthConstant.ROLE_ADMIN_ID));
                boolean isSuperUser = Objects.equals(userOrgRoleDto.getId(), AuthConstant.ROOT_USER_ID);

                processIsAdminSuperUser(userDto, isAdmin, isSuperUser);
            } else {
                userDto.setRoles(Collections.emptyList());
                userDto.setIsSuperUser(0);
                userDto.setIsAdmin(0);
            }
            userDtos.add(userDto);
        }
        this.batchSetDefaultIfNoAvatar(userDtos);

        return userDtos;
    }

    @Override
    public UserDto doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole) {
        ParamCheck.notBlank(userModel.getId(), AuthBasei18nCode.USER_ID);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userModel, userDto);
        // 过期状态处理
        processExpiredStatus(userDto);

        if (needOrg) {
            List<OrgBO> orgBOS = orgUserService.listOrgsByUserId(userModel.getId());
            List<String> orgIds = orgBOS.stream().map(BaseModel::getId).collect(Collectors.toList());

            //List<List<String>> allorgpath = orgService.getBathPathToList(orgs);
            List<List<String>> allorgpath = orgBOS.stream().map(o -> CollectionUtil.toList(StringUtils.split(o.getPath(), AuthConstant.ORG_PATH_SEPARATOR))).collect(Collectors.toList());
            userDto.setOrgs(orgBOS);
            userDto.setOrgIds(orgIds);
            userDto.setOrgpaths(allorgpath);
        }


        if(needRole) {
            List<RoleModel> roles = userRoleService.listByUserId(userModel.getId());
            userDto.setRoles(roles);

            boolean isAdmin = roles.stream().anyMatch(r -> Objects.equals(r.getId(), AuthConstant.ROLE_ADMIN_ID));
            boolean isSuperUser = Objects.equals(userModel.getId(), AuthConstant.ROOT_USER_ID);

            processIsAdminSuperUser(userDto, isAdmin, isSuperUser);
        }

        return userDto;
    }



//    @Override
//    public PageInfo onlineReport(UserOnlineReportPageRequest pageRequest) {
//        return userOnlineReportService.page(pageRequest);
//    }

    @Override
    public List<UserModel> listNotAudited() {
        List<UserModel> userModels = getModelMapper().list(new HumanCondition().filterEqual("is_audited", "0"));
        userModels.stream().forEach(userModel -> setDefaultIfNoAvatar(userModel));
        return userModels;
    }

    @Override
    public List<UserDto> listByUserIds(String[] userIds) {
        List<UserOrgRoleDto> userOrgRoleDtos = getByUserIds(userIds);
        List<UserDto> userDtos = userOrgRoleDtos2UserDtos(userOrgRoleDtos);
        userDtos.stream().forEach(userDto -> setDefaultIfNoAvatar(userDto));
        return userDtos;
    }

    @Override
    public List<UserModel> listByIds(String[] userIds) {
        return getModelMapper().list(new HumanCondition().filterIn(UserModel::getId, userIds));
    }

    @Override
    public int totalUserCnt() {
        int userCnt = getModelMapper().total(new HumanCondition());
        return userCnt;
    }


    @Override
    public void setModelMapper(UserMapper modelMapper) {
        super.setModelMapper(modelMapper);
    }


    @Override
    public String getDefaultUserAvatar() {
        return authConfig.getDefaultUserAvatar();
    }


    private void processIsAdminSuperUser(UserDto userDto, boolean isAdmin, boolean isSuperUser) {
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
    }

    private UserModel importUser2UserModel(ImportUser importUser) {
        if (Objects.isNull(importUser)) {
            return null;
        }
        UserModel userModel =
                UserModel.builder().username(importUser.getUsername())
                        .name(importUser.getName())
                        // 此处不需要加密，后面的create()方法会加密
                        .password(importUser.getPassword())
                        .build();
        return userModel;
    }


    private ExportUser user2ExportUser(UserModel user) {
        if (Objects.isNull(user)) {
            return null;
        }
        ExportUser exportUser = ExportUser.builder().username(user.getUsername()).name(user.getName()).build();

        // org path
        List<OrgBO> orgList = orgUserService.listOrgsByUserId(user.getId());
        // 调整为跟前端逻辑一样，如果存在多个组织，则选择主键最大的一个
        Optional<OrgBO> optional = orgList.stream().max(Comparator.comparing(OrgBO::getPk));
        optional.ifPresent(o -> exportUser.setOrg(orgService.getPath(o)));

        String statusNormal = MessageCommon.get(AuthBasei18nCode.USER_STATUS_NORMAL);
        String statusForbidden = MessageCommon.get(AuthBasei18nCode.USER_STATUS_FORBIDDEN);
        String statusExpired = MessageCommon.get(AuthBasei18nCode.USER_STATUS_EXPIRED);
        String statusNotAudit = MessageCommon.get(AuthBasei18nCode.USER_STATUS_NOT_AUDIT);
        exportUser.setStat(statusNormal);
        // 状态展示优先级 禁用 > 过期 > 待审核
        if (user.getIsAudited().equals(UserModel.Status.NOT_AUDITED)) {
            exportUser.setStat(statusNotAudit);
        }

        // 过期
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            exportUser.setStat(statusExpired);
        }

        // 禁用
        if (user.getIsEnabled().equals(UserModel.Status.DISABLED)) {
            exportUser.setStat(statusForbidden);
        }

        // role
        List<RoleModel> roleList = userRoleService.listByUserId(user.getId());
        String roles = roleList.stream().map(RoleModel::getName).collect(Collectors.joining("/"));
        log.info("roles:{}", roles);
        exportUser.setRoles(roles);

        return exportUser;
    }


    private ExportUserEn user2ExportUserEn(UserModel userModel) {
        ExportUser exportUser = user2ExportUser(userModel);

        if (Objects.nonNull(exportUser)) {
            ExportUserEn exportUserEn = ExportUserEn.builder().build();
            BeanUtils.copyProperties(exportUser, exportUserEn);
            return exportUserEn;
        }
        return null;
    }


    private void deleteReleated(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        if (Objects.equals(AuthConstant.ROOT_USER_ID, userId)) {
            log.warn("can not delete admin user!");
            throw new MpRuntimeException(AuthErrorCode.USER_CANNOT_DELETE_ADMIN);
        }

        boolean cleanUserRolesSuccess = userRoleService.deleteByUserId(userId);
        boolean cleanUserOrgsSuccess = orgUserService.deleteByUserId(userId);
        boolean cleanUserTargetsSuccess = userPermissionService.deleteByUserId(userId);
        boolean cleanUserTenantsSuccess = tenantUserService.deleteByUserId(userId);

        if(!cleanUserRolesSuccess || !cleanUserOrgsSuccess || !cleanUserTargetsSuccess || !cleanUserTenantsSuccess) {
            log.info("cleanUserRolesSuccess:{}, cleanUserOrgsSuccess:{}, cleanUserTargetsSuccess:{}, cleanUserTenantsSuccess:{}", cleanUserRolesSuccess, cleanUserOrgsSuccess, cleanUserTargetsSuccess, cleanUserTenantsSuccess);
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
    }

    public List<UserOrgRoleDto> getByUserIds(String[] userIds) {
        HumanCondition userCondition = new HumanCondition()
                .filterIn(UserModel::getId, userIds)
                .distinct()
                .orderBy(UserModel::getPk);
        List<UserModel> userModels = getModelMapper().list(userCondition);

        HumanCondition orgUserCondition = new HumanCondition()
                .filterIn(OrgUserModel::getUser, userIds)
                .select(OrgUserModel::getUser)
                .selectAs(GroupConcat.f(OrgUserModel::getOrg).distinct(), UserOrgRoleDto.ORGS)
                .groupBy(OrgUserModel::getUser);
        List<Map<String, Object>> orgs = orgUserMapper.originList(orgUserCondition);
        Map<String, String> orgMap = new HashMap<>();
        for (Map<String, Object> userOrg : orgs) {
            orgMap.put((String) userOrg.get(UserOrgRoleDto.USER), (String) userOrg.get(UserOrgRoleDto.ORGS));
        }

        HumanCondition userRoleCondition = new HumanCondition()
                .filterIn(UserRoleModel::getUser, userIds)
                .select(UserRoleModel::getUser)
                .selectAs(GroupConcat.f(UserRoleModel::getRole).distinct(), UserOrgRoleDto.ROLES)
                .groupBy(UserRoleModel::getUser);
        List<Map<String, Object>> roles = userRoleMapper.originList(userRoleCondition);
        Map<String, String> roleMap = new HashMap<>();
        for (Map<String, Object> userRole : roles) {
            roleMap.put((String) userRole.get(UserOrgRoleDto.USER), (String) userRole.get(UserOrgRoleDto.ROLES));
        }

        List<UserOrgRoleDto> userOrgRoleDtos = new ArrayList<>();
        for (UserModel userModel : userModels) {
            UserOrgRoleDto userOrgRoleDto = new UserOrgRoleDto();
            BeanUtils.copyProperties(userModel, userOrgRoleDto);
            userOrgRoleDto.setOrgs(orgMap.get(userModel.getId()));
            userOrgRoleDto.setRoles(roleMap.get(userModel.getId()));
            userOrgRoleDtos.add(userOrgRoleDto);
        }

        return userOrgRoleDtos;
    }
}

class GroupConcat extends Aggregation<GroupConcat> {

    public static Function<String, String> genDistinct() {
        return column -> String.format("distinct (%s)", column);
    }

    public GroupConcat distinct() {
        addCalc(genDistinct());
        return this;
    }

    public static GroupConcat f(String fieldName) {
        var aggregation = new GroupConcat();
        aggregation.setFieldName(fieldName);
        return aggregation;
    }

    public static <T> GroupConcat f(SerializableFunction<T> getter) {
        String fieldName = BeanUtils.getBeanGetterFieldName(getter);
        return f(fieldName);
    }

    @Override
    public String getFuncName() {
        return "group_concat";
    }
}
