package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserHeartbeatRequest;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.SysConfigService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields, UserQueryParams>
        implements UserService {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private SysConfigService sysConfigService;

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
    private AuthDesensitizedService desensitizedService;

    @Autowired
    private OperateUserPermissionService opUserPermCheckService;

    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public UserDto getUserDto(String id) {
        UserModel userModel = getModelMapper().get(id);

        UserDto userDto = userModel2UserDto(userModel);
        desensitizedService.desensitiveUserDtos(Arrays.asList(userDto));
        return userDto;
    }


    @Override
    public UserDto getCurrentUserDto() {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.isNull(userModel)) {
            log.info("user not login!");
            return null;
        }

        UserDto userDto = userModel2UserDto(userModel);
        desensitizedService.desensitiveUserDtos(Arrays.asList(userDto));
        return userDto;
    }

    @Override
    public ListObjServiceResult list(UserQueryParams queryParams) {
        ListObjServiceResult<UserModel> listObjSrvResult = super.list(queryParams);
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(queryParams.getIds()));

        List<UserDto> userDtos;
        if (Objects.isNull(listObjSrvResult.getSource()) || CollectionUtil.isEmpty(listObjSrvResult.getSource().getObjs())) {
            return listObjSrvResult;
        }

        List<UserModel> users = listObjSrvResult.getSource().getObjs();
        userDtos = users.stream().map(u -> {
            UserDto userDto = userModel2UserDto(u);
            return userDto;
        }).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(userDtos)) {
            // 转换
            desensitizedService.desensitiveUserDtos(userDtos);
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
        Assert.notNull(createRequest, "userDto不能为空");

        opUserPermCheckService.checkByTargetRoleIds(createRequest.getRoleIds());

        // 保存用户
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(createRequest, userModel);

        String decryptedPwd = PasswordUtils.desDecrypt(createRequest.getPassword());
        userModel.setPassword(decryptedPwd);

        createCheckService.check(userModel);

        chooseDefaultAvatar(userModel);
        // create中会对密码加密，外层不需要再次加密
        ObjServiceResult<UserModel> result = create(userModel);
        ResultUtils.throwExceptionIfError(result, "创建用户失败！");

        UserModel userCreated = result.getSource().getObj();
        String userId = userCreated.getId();
        log.info("user created id:{}", userId);

        // 保存组织-用户关系
        if (CollectionUtil.isNotEmpty(createRequest.getOrgIds())) {
            createRequest.getOrgIds().forEach(id -> {
                boolean isOrgExist = orgService.exists(id);
                if (!isOrgExist) {
                    log.error("组织id:{}不存在", id);
                    throw new MpRuntimeException("组织id：" + id + "不存在！");
                }
                OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(id).build();
                var re = orgUserService.create(orgUserModel);
                ResultUtils.throwExceptionIfError(re, "创建组织-用户关系失败！");
            });
        }


        // 保存角色-用户关系
        if (CollectionUtil.isNotEmpty(createRequest.getRoleIds())) {
            createRequest.getRoleIds().forEach(id -> {
                boolean isRoleExist = roleService.exists(id);
                if (!isRoleExist) {
                    log.error("角色id:{}不存在", id);
                    throw new MpRuntimeException("角色id：" + id + "不存在！");
                }
                UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(id).build();
                var re = userRoleService.create(userRoleModel);
                ResultUtils.throwExceptionIfError(re, "创建用户-角色关系失败！");
            });
        }

        return userModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel modify(UserRequest modifyRequest) {

        opUserPermCheckService.checkByTargetRoleIds(modifyRequest.getRoleIds());

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
            throw new MpRuntimeException("修改用户失败！");
        }

        UserModel userModified = result.getSource().getObj();
        String userId = userModified.getId();
        log.info("user modified id:{}", userId);

        // 保存组织-用户关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getOrgIds())) {
            // 清除原来所有用户关联的组织
            orgUserService.deleteByUserId(userId);

            modifyRequest.getOrgIds().forEach(id -> {
                boolean isOrgExist = orgService.exists(id);
                if (!isOrgExist) {
                    log.error("组织id:{}不存在", id);
                    throw new MpRuntimeException("组织id：" + id + "不存在！");
                }
                OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(id).build();
                var re = orgUserService.create(orgUserModel);
                ResultUtils.throwExceptionIfError(re, "重新创建组织-用户关系失败！");
            });
        }


        // 保存角色-用户关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getRoleIds())) {
            // 清除原来所有用户关联的角色
            userRoleService.deleteByUserId(userId);

            modifyRequest.getRoleIds().forEach(id -> {
                boolean isRoleExist = roleService.exists(id);
                if (!isRoleExist) {
                    log.error("角色id:{}不存在", id);
                    throw new MpRuntimeException("角色id：" + id + "不存在！");
                }
                UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(id).build();
                var re = userRoleService.create(userRoleModel);
                ResultUtils.throwExceptionIfError(re, "重新创建用户-角色关系失败！");
            });
        }

        return userModel;
    }

    @Override
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("username", userName));
        // 登录接口用了这个方法，不能对密码字段做脱敏处理
        return userModel;
    }


    @Override
    public UserModel getByIdent(String ident) {
        log.info("ident:{}", ident);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("identifier", ident));
        desensitizedService.desensitiveUserModels(Arrays.asList(userModel));
        return userModel;
    }

    @Override
    public UserModel getByMobile(String mobile) {
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
        desensitizedService.desensitiveUserModels(Arrays.asList(userModel));
        return userModel;
    }

    @Override
    public boolean resetSelfPwd(String oldPwd, String newPwd) {
        var currentUser = UserUtils.getLoginUser();
        Assert.notNull(currentUser, AuthConstant.ERROR_CODE_PREFIX + "account_not_login");

        // 旧密码是否正确
        boolean isOldPwdMatch = PasswordUtils.getPasswordEncoder().matches(oldPwd, currentUser.getPassword());
        if (!isOldPwdMatch) {
            log.warn("旧密码不正确！");
        }
        Assert.isTrue(isOldPwdMatch, AuthConstant.ERROR_CODE_PREFIX + "incorrect_old_credentials");

        // 检查新密码强度是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(newPwd), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");

        UserModel userUpdate = UserModel.builder().password(newPwd).build();
        userUpdate.setId(currentUser.getId());
        update(userUpdate);
        return true;
    }

    @Override
    public boolean resetPwd(String userId, String newPwd) {
        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(newPwd), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

        var source = get(userId).getSource();
        UserModel user = Objects.isNull(source) ? null : source.getObj();
        Assert.notNull(user, AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");

        UserModel userUpdate = UserModel.builder().id(user.getId()).password(newPwd).build();
        update(userUpdate);
        return true;
    }

    @Override
    public boolean disable(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsEnabled(), UserModel.Status.ENABLED)) {
                UserModel updateUser = UserModel.builder().id(userId).isEnabled(UserModel.Status.DISABLED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("id为{}的用户状态不是正常状态！", userId);
            throw new MpRuntimeException("id为:" + userId + "的用户状态不是正常状态！");
        } else {
            log.error("id为:" + userId + "的用户不存在！");
        }

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchDisable(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
        opUserPermCheckService.checkByTargetUserIds(userIds);
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
        Assert.notBlank(userId, "userId不能为空！");
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsEnabled(), UserModel.Status.DISABLED)) {
                UserModel updateUser = UserModel.builder().id(userId).isEnabled(UserModel.Status.ENABLED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("id为{}的用户状态不是禁用状态！", userId);
            throw new MpRuntimeException("id为:" + userId + "的用户状态不是禁用状态！");
        } else {
            log.error("id为{}的用户不存在！", userId);
        }

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchEnable(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
        opUserPermCheckService.checkByTargetUserIds(userIds);
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
        Assert.notBlank(userId, "userId不能为空！");
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getIsAudited(), UserModel.Status.NOT_AUDITED)) {
                UserModel updateUser = UserModel.builder().id(userId).isAudited(UserModel.Status.AUDITED).build();
                return getModelMapper().update(updateUser);
            }
            log.error("id为{}的用户状态不是挂起状态！", userId);
            throw new MpRuntimeException("id为:" + userId + "的用户状态不是挂起状态！");
        } else {
            log.error("id为{}的用户不存在！", userId);
        }

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchApprove(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
        opUserPermCheckService.checkByTargetUserIds(userIds);
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
//            String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("excel/import_template.xls")).getPath();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("excel/import_template.xls");
            String fileName = "用户列表.xls";

            InputStream fis = new BufferedInputStream(is);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
//            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();

        } catch (IOException e) {
            log.error("{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("下载模板错误！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(List<ImportUser> list) {

        // 对导入的用户进行权限检查，低权限用户不能导入高权限用户
        Set<String> roleNameSet = new HashSet<>();
        List<String> roleNamesList = list.stream().map(ImportUser::getRoles).collect(Collectors.toList());
        roleNamesList.forEach(roleNames -> {
            if (StringUtils.isNotBlank(roleNames)) {
                Set<String> subRoleNameSet = Arrays.stream(roleNames.split(",")).map(String::trim).collect(Collectors.toSet());
                roleNameSet.addAll(subRoleNameSet);
            }
        });

        if (CollectionUtil.isNotEmpty(roleNameSet)) {
            opUserPermCheckService.checkByTargetRoleNames(roleNameSet.stream().collect(Collectors.toList()));
        }

        for (ImportUser importUser : list) {
            // 1.保存用户信息
            UserModel userModel = importUser2UserModel(importUser);
            if (Objects.isNull(userModel)) {
                continue;
            }
//            String userId = IdGenerator.generateUUID();
//            log.warn("userId:{}", userId);
//            userModel.setId(userId);
            chooseDefaultAvatar(userModel);
            var result = create(userModel);
            ResultUtils.throwExceptionIfError(result, "创建用户失败！");
            UserModel userCreated = result.getSource().getObj();
            String userId = userCreated.getId();

            // 2. 保存用户-角色关系
            String roles = importUser.getRoles().trim();
            Arrays.stream(StringUtils.split(roles, ",")).forEach(rname -> {
                RoleModel roleModel = roleService.getByName(rname.trim());
                if (Objects.nonNull(roleModel)) {
                    UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(roleModel.getId()).build();
                    var re = userRoleService.create(userRoleModel);
                    ResultUtils.throwExceptionIfError(re, "创建用户-角色关系失败！");
                }
            });

            // 3. 保存组织-用户关系
            String orgPaths = importUser.getOrgpath().trim();
            Arrays.stream(StringUtils.split(orgPaths, ",")).forEach(path -> {
                OrgModel orgModel = orgService.getByPath(path);
                if (Objects.nonNull(orgModel)) {
                    OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(orgModel.getId()).build();
                    var re = orgUserService.create(orgUserModel);
                    ResultUtils.throwExceptionIfError(re, "创建组织-用户关系失败！");
                }
            });

        }
    }

    @Override
    public List<ExportUser> exportExcel(List<UserModel> users) {
        List<ExportUser> exportUserList = users.stream().map(this::user2ExportUser).filter(Objects::nonNull).collect(Collectors.toList());
        return exportUserList;
    }


    @Override
    public boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException {
        Assert.notBlank(userId, "userId不能为空！");
        opUserPermCheckService.checkByTargetUserIds(Arrays.asList(userId));

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
                throw new MpRuntimeException("头像上传失败！");
            }

        } else {
            log.error("id为{}的用户不存在！", userId);
            throw new MpRuntimeException("id为" + userId + "的用户不存在！");
        }

    }


    /**
     * 处理用户过期状态，只有当用户状态为正常状态，且过期时间已到的时候，才会更改为过期状态
     *
     * @param user
     */
    public void processExpiredStatus(UserDto user) {
        if (Objects.isNull(user) || Objects.isNull(user.getExpiredTime())) {
            return;
        }

        user.setIsExpired(UserModel.Status.NOT_EXPIRED);

        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            log.info("用户状态已经过期，准备更新数据库用户状态！ username:{}", user.getUsername());
            user.setIsExpired(UserModel.Status.EXPIRED);
        }
    }


    @Override
    @Cacheable(value = "users_cache", key = "'usermodel_to_userdto_'+#userModel.getId()")
    public UserDto userModel2UserDto(UserModel userModel) {
        log.info("not use cache for userId:{}", userModel.getId());
        if (Objects.isNull(userModel)) {
            return null;
        }
        return doModel2Dto(userModel, true, true);
    }


    @Override
    @Cacheable(value = "users_cache", key = "'usermodel_to_userdto_withoutorg_'+#userModel.getId()")
    public UserDto userModel2UserDtoWithoutOrg(UserModel userModel) {
        log.info("not use cache for userId:{}", userModel.getId());
        if (Objects.isNull(userModel)) {
            return null;
        }
        return doModel2Dto(userModel, false, true);
    }


    @Override
    public UserDto doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole) {
        if (Objects.isNull(userModel)) {
            return null;
        }

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
            boolean isSuperUser = roles.stream().anyMatch(r -> r.getIdentifier().equals(AuthConstant.ROLE_SUPERUSER));

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

    @Override
    public void heartbeat(UserHeartbeatRequest hbrequest) {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.nonNull(userModel)) {
            UserHeartbeatModel heartbeat = new UserHeartbeatModel();
            heartbeat.setUser(userModel.getId());

            userHeartbeatService.create(heartbeat);
        }
    }

//    @Override
//    public PageInfo onlineReport(UserOnlineReportPageRequest pageRequest) {
//        return userOnlineReportService.page(pageRequest);
//    }

    @Override
    public List<UserModel> listNotAudited() {
        List<UserModel> userModels = getModelMapper().list(new HumanCondition().filterEqual("is_audited", "0"));
        desensitizedService.desensitiveUserModels(userModels);
        return userModels;
    }

    @Override
    public int totalUserCnt() {
        return getModelMapper().getUserCnt();
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
//        String paths = orgList.stream().map(o -> orgService.calculatePathByOrgId(o.getId())).collect(Collectors.joining(","));
        Optional<OrgModel> optional = orgList.stream().max(Comparator.comparing(OrgModel::getPk));
        optional.ifPresent(o -> exportUser.setOrg(o.getPath()));

        exportUser.setStat("正常");
        // 状态展示优先级 禁用 > 过期 > 待审核
        if (user.getIsAudited().equals(UserModel.Status.NOT_AUDITED)) {
            exportUser.setStat("待审核");
        }

        // 过期
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            exportUser.setStat("已过期");
        }

        // 禁用
        if (user.getIsEnabled().equals(UserModel.Status.DISABLED)) {
            exportUser.setStat("禁用");
        }

        // role
        List<RoleModel> roleList = userRoleService.listByUserId(user.getId());
        String roles = roleList.stream().map(RoleModel::getName).collect(Collectors.joining("/"));
        exportUser.setRoles(roles);

        return exportUser;
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
            throw new MpRuntimeException("删除失败！");
        }
    }

    private void chooseDefaultAvatar(UserModel userModel) {
        if (Objects.isNull(userModel) || StringUtils.isNotBlank(userModel.getAvatar())) {
            return;
        }
        List<AvatarModel> avatarModels = avatarService.listAll();
        if (CollectionUtil.isNotEmpty(avatarModels)) {
            userModel.setAvatar(avatarModels.get(0).getUrl());
        }
    }


}
