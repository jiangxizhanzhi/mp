package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ExportUserEn;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.DefaultImgService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields, UserQueryParams>
        implements UserService {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FastDfsConfig fastDfsConfig;

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
    private UserHeartbeatService userHeartbeatService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private DefaultImgService defaultImgService;


    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public UserDto getUserDto(String id) {
        UserModel userModel = getModelMapper().get(id);

        UserDto userDto = userModel2UserDto(userModel);

        return userDto;
    }


    @Override
    public UserDto getCurrentUserDto() {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.isNull(userModel)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
        }
        UserDto userDto = userModel2UserDto(userModel);

        return userDto;
    }

    @Override
    public ListObjServiceResult list(UserQueryParams queryParams) {
        ListObjServiceResult<UserModel> listObjSrvResult = super.list(queryParams);

        List<UserDto> userDtos;
        if (Objects.isNull(listObjSrvResult.getSource()) || CollectionUtil.isEmpty(listObjSrvResult.getSource().getObjs())) {
            return listObjSrvResult;
        }

        List<UserModel> users = listObjSrvResult.getSource().getObjs();
        userDtos = users.stream().map(this::userModel2UserDto).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(userDtos)) {
            // 转换
            ListObjServiceResult<UserDto> result = new ListObjServiceResult<>();
            BeanUtils.copyProperties(listObjSrvResult, result);
            result.getSource().setObjs(userDtos);
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
        return super.update(obj, fields);
    }

    @Override
    public ServiceResult<Integer> bulkCreate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);

        return super.bulkCreate(objs);
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);
        return super.bulkUpdate(objs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean>  delete(String userId) {
        deleteReleated(userId);

        return super.delete(userId);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] userIds) {

        for (String userId : userIds) {
            deleteReleated(userId);
        }

        return super.bulkDelete(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel add(UserRequest createRequest) {
        ParamCheck.notNull(createRequest, AuthBasei18nCode.REQUEST_PARAMS);

        // 保存用户
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(createRequest, userModel);

        String decryptedPwd = PasswordUtils.desDecrypt(createRequest.getPassword());
        userModel.setPassword(decryptedPwd);

        if (StringUtils.isBlank(userModel.getIdentifier())) {
            // 如果ident为空，设置为null
            userModel.setIdentifier(null);
        }

        createCheckService.check(userModel);

        if (StringUtils.isEmpty(userModel.getAvatar())) {
            userModel.setAvatar(getDefaultUserAvatar());
        }
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

        return userModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel modify(UserRequest modifyRequest) {
        // 保存用户
        UserModel userModel = new UserModel();
        org.springframework.beans.BeanUtils.copyProperties(modifyRequest, userModel);

        if (StringUtils.isNotBlank(modifyRequest.getPassword())) {
            String decryptedPwd = PasswordUtils.desDecrypt(modifyRequest.getPassword());
            userModel.setPassword(decryptedPwd);
        } else {
            userModel.setPassword(null);
        }


        updateCheckService.check(userModel);

        // udpate中会对密码加密，外层不需要再次加密
        ObjServiceResult<UserModel> result = update(userModel);

        if (Objects.isNull(result.getSource()) || Objects.isNull(result.getSource().getObj())) {
            log.error("update user failed！");
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }

        UserModel userModified = result.getSource().getObj();
        String userId = userModified.getId();
        log.info("user has been modified id:{}", userId);

        if (StringUtils.isBlank(userModel.getIdentifier())) {
            // 若identifier为空，则设置identifier为null @See http://10.10.10.10/biz/bug-view-2390.html
            getModelMapper().updateIdentToNull(userId);
        }

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

        return userModel;
    }

    @Override
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("username", userName));
        return userModel;
    }


    @Override
    public List<UserModel> listByUserName(List<String> userNames) {
        List<UserModel> userModels = getModelMapper().listByUserNames(userNames);
        return userModels;
    }

    @Override
    public UserModel getByIdent(String ident) {
        log.info("ident:{}", ident);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("identifier", ident));
        return userModel;
    }

    @Override
    public UserModel getByMobile(String mobile) {
        log.info("mobile:{}", mobile);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
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
        return true;
    }

    @Override
    public boolean resetPwd(String userId, String newPwd) {
        // 密码是否合法
        if (PasswordUtils.isPasswordStrengthLowLevel(newPwd)) {
            throw new MpRuntimeException(AuthErrorCode.PASSWORD_STRENGTH_INVALID);
        }

        var source = get(userId).getSource();
        UserModel user = Objects.isNull(source) ? null : source.getObj();
        if (Objects.isNull(user)) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
        }

        UserModel userUpdate = UserModel.builder().id(userId).password(newPwd).initCheck(UserModel.INT_CHECK_NO).build();
        update(userUpdate);
        return true;
    }

    @Override
    public boolean disable(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
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

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchDisable(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                disable(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }

        return errMsg.toString();
    }

    @Override
    public boolean enable(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
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

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchEnable(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                enable(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }

        return errMsg.toString();
    }


    @Override
    public boolean approve(String userId) {
        ParamCheck.notBlank(userId, AuthBasei18nCode.USER_ID);
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

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchApprove(List<String> userIds) {
        ParamCheck.notEmpty(userIds, AuthBasei18nCode.USER_LIST);
        StringBuilder errMsg = new StringBuilder();
        for (String userId : userIds) {
            try {
                approve(userId);
            } catch (Exception e) {
                errMsg.append(e.getMessage());
            }
        }

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
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(List<ImportUser> list) {
        log.info("Now import users from excel...");
        // 初始化默认头像信息
        for (ImportUser importUser : list) {
            log.info("importUser:{}", importUser);
            // 1.保存用户信息
            UserModel userModel = importUser2UserModel(importUser);
            if (Objects.isNull(userModel)) {
                continue;
            }

            if (StringUtils.isEmpty(userModel.getAvatar())) {
                userModel.setAvatar(getDefaultUserAvatar());
            }

            var result = create(userModel);
            ResultUtils.throwExceptionIfError(result, AuthErrorCode.USER_ADD_ERROR);
            UserModel userCreated = result.getSource().getObj();
            String userId = userCreated.getId();

            // http://10.10.10.10/biz/bug-view-4861.html
            // 2. 保存用户-角色关系 之前的步骤已经处理过roleIdList了
            if (CollectionUtil.isNotEmpty(importUser.getRoleIdList())) {
                importUser.getRoleIdList().forEach(rid -> {
                            UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(rid).build();
                            var re = userRoleService.create(userRoleModel);
                            ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ROLE_ADD_ERROR);
                        }
                );
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
            }

            log.info("import success...");
        }
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
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            // 上传服务器
            String filepath = null;
            try {
                filepath = fastDFSClient.uploadFileWithMultipart(multipartFile, FdfsGroupEnum.DEFAULT.toString());
                log.info("uploaded avatar success! filepath:{}", filepath);
            } catch (MpRuntimeException e) {
                log.error("uploaded error! {}", ExceptionUtil.stacktraceToString(e));
                throw e;
            }

            if (StringUtils.isNotBlank(filepath)) {
                // 获取当前集群配置
                FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
                String httpUrl = group.getFileServerAddr() + File.separator + filepath;

                UserModel updateUser = UserModel.builder().id(userId).avatar(httpUrl).build();
                return getModelMapper().update(updateUser);
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
    public UserDto doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole) {
        ParamCheck.notBlank(userModel.getId(), AuthBasei18nCode.USER_ID);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userModel, userDto);
        // 过期状态处理
        processExpiredStatus(userDto);

        if (needOrg) {
            List<OrgModel> orgs = orgUserService.listOrgsByUserId(userModel.getId());
            List<String> orgIds = orgs.stream().map(BaseModel::getId).collect(Collectors.toList());

            List<List<String>> allorgpath = orgs.stream().map(o -> CollectionUtil.toList(StringUtils.split(o.getPath(), AuthConstant.ORG_PATH_SEPARATOR))).collect(Collectors.toList());
            userDto.setOrgs(orgs);
            userDto.setOrgIds(orgIds);
            userDto.setOrgpaths(allorgpath);
        }


        if(needRole) {
            List<RoleModel> roles = userRoleService.listByUserId(userModel.getId());
            userDto.setRoles(roles);

            boolean isAdmin = roles.stream().anyMatch(r -> r.getIdentifier().equals(AuthConstant.ROLE_ADMIN));
            boolean isSuperUser = userModel.getId().equals(AuthConstant.ROOT_USER_ID);

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

        return userDto;

    }


//    @Override
//    public PageInfo onlineReport(UserOnlineReportPageRequest pageRequest) {
//        return userOnlineReportService.page(pageRequest);
//    }

    @Override
    public List<UserModel> listNotAudited() {
        List<UserModel> userModels = getModelMapper().list(new HumanCondition().filterEqual("is_audited", "0"));
        return userModels;
    }

    @Override
    public List<UserDto> listByUserIds(String[] userIds) {
        List<UserModel> userModels = getModelMapper().list(new HumanCondition().filterIn("id", userIds).orderBy("pk"));

        List<UserDto> userDtos = userModels.stream().map(u -> {
            UserDto userDto = userModel2UserDto(u);
            return userDto;
        }).collect(Collectors.toList());

        return userDtos;
    }

    @Override
    public int totalUserCnt() {
        return getModelMapper().getUserCnt();
    }


    @Override
    public void setModelMapper(UserMapper modelMapper) {
        super.setModelMapper(modelMapper);
    }

    @Override
    public boolean isAdmin(String userId) {
        List<RoleModel> roleModels = userRoleService.listByUserId(userId);
        boolean isAdmin = roleModels.stream().anyMatch(r -> r.getIdentifier().equals(AuthConstant.ROLE_ADMIN));
        return isAdmin;
    }


    @Override
    public String getDefaultUserAvatar() {
        List<String> avatarList = defaultImgService.getFileByDefaultImgDirEnum(DefaultImgDirEnum.AVATAR);
        String avatar = "/api/mp" + avatarList.get(0);
        return avatar;
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
        List<OrgModel> orgList = orgUserService.listOrgsByUserId(user.getId());
        // 调整为跟前端逻辑一样，如果存在多个组织，则选择主键最大的一个
        Optional<OrgModel> optional = orgList.stream().max(Comparator.comparing(OrgModel::getPk));
        optional.ifPresent(o -> exportUser.setOrg(o.getPath()));

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

    @Override
    public List<UserModel> queryAllUsers() {
        return getModelMapper().queryAllUsers();
    }
}
