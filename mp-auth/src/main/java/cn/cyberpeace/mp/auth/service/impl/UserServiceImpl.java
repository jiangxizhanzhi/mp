package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.IdGenerator;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields, UserQueryParams>
        implements UserService {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FastDfsConfig fastDfsConfig;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserCreateCheckService createCheckService;

    @Autowired
    private UserUpdateCheckService updateCheckService;

    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ObjServiceResult<UserModel> get(String id) {
        ObjServiceResult<UserModel> objServiceResult = super.get(id);

        if (Objects.isNull(objServiceResult.getSource()) || Objects.isNull(objServiceResult.getSource().getObj())) {
            return objServiceResult;
        }
        UserModel userModel = objServiceResult.getSource().getObj();
        processExpiredStatus(userModel);
        return objServiceResult;
    }

    @Override
    public ListObjServiceResult list(UserQueryParams queryParams) {
        ListObjServiceResult<UserModel> listObjSrvResult = super.list(queryParams);

        List<UserDto> userDtos;
        if (Objects.isNull(listObjSrvResult.getSource()) || CollectionUtil.isEmpty(listObjSrvResult.getSource().getObjs())) {
            return listObjSrvResult;
        }

        List<UserModel> users = listObjSrvResult.getSource().getObjs();
        userDtos = users.stream().map(u -> {
            processExpiredStatus(u);

            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(u, userDto);
            String allorgpath = orgUserService.getAllOrgPathByUserId(u.getId());
            userDto.setOrgpath(allorgpath);

            List<RoleModel> roles = userRoleService.listByUserId(u.getId());
//            String allroles = roles.stream().map(r -> r.getIdentifier()).collect(Collectors.joining(","));
            userDto.setRoles(roles);
            return userDto;
        }).collect(Collectors.toList());

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
        // 查看系统设置，注册用户是否需要审批
        if (authConfig.isNeedRegistrationAudit()) {
            log.info("need registration audit...");
            obj.setStatus(UserModel.Status.PENDING);
        }
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
        // 查看系统设置，注册用户是否需要审批
        if (authConfig.isNeedRegistrationAudit()) {
            log.info("need registration audit...");
            objs.forEach(o -> o.setStatus(UserModel.Status.PENDING));
        }
        return super.bulkCreate(objs);
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<UserModel> objs) {
        objs.forEach(UserModel::encodePassword);
        return super.bulkUpdate(objs);
    }

    @Override
    public ServiceResult<Boolean> delete(String id) {
        // todo 删除用户-组织，用户-角色关联关系
        return super.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel add(UserRequest createRequest) {
        Assert.notNull(createRequest, "userDto不能为空");

        // 保存用户
        UserModel userModel = new UserModel();
        org.springframework.beans.BeanUtils.copyProperties(createRequest, userModel);

        String decryptedPwd = PasswordUtils.desDecrypt(createRequest.getPassword());
        userModel.setPassword(decryptedPwd);

        if (StringUtils.isBlank(userModel.getIdentifier())) {
            // 如果ident为空，则随机生成一个
            userModel.setIdentifier(String.valueOf(IdGenerator.generateGlobalId()));
        }

        createCheckService.check(userModel);

        // create中会对密码加密，外层不需要再次加密
        ObjServiceResult<UserModel> result = create(userModel);

        if (Objects.isNull(result.getSource()) || Objects.isNull(result.getSource().getObj())) {
            throw new MpRuntimeException("创建用户失败！");
        }

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
                orgUserService.create(orgUserModel);
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
                userRoleService.create(userRoleModel);
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

        updateCheckService.check(userModel);

        // create中会对密码加密，外层不需要再次加密
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
            orgUserService.cleanUserOrgs(userId);

            modifyRequest.getOrgIds().forEach(id -> {
                boolean isOrgExist = orgService.exists(id);
                if (!isOrgExist) {
                    log.error("组织id:{}不存在", id);
                    throw new MpRuntimeException("组织id：" + id + "不存在！");
                }
                OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(id).build();
                orgUserService.create(orgUserModel);
            });
        }


        // 保存角色-用户关系
        if (CollectionUtil.isNotEmpty(modifyRequest.getRoleIds())) {
            // 清除原来所有用户关联的角色
            userRoleService.cleanUserRoles(userId);

            modifyRequest.getRoleIds().forEach(id -> {
                boolean isRoleExist = roleService.exists(id);
                if (!isRoleExist) {
                    log.error("角色id:{}不存在", id);
                    throw new MpRuntimeException("角色id：" + id + "不存在！");
                }
                UserRoleModel userRoleModel = UserRoleModel.builder().user(userId).role(id).build();
                userRoleService.create(userRoleModel);
            });
        }

        return userModel;
    }

    @Override
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("username", userName));
        processExpiredStatus(userModel);
        return userModel;
    }


    @Override
    public UserModel getByIdent(String ident) {
        log.info("ident:{}", ident);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("identifier", ident));
        processExpiredStatus(userModel);
        return userModel;
    }

    @Override
    public UserModel getByMobile(String mobile) {
        log.info("mobile:{}", mobile);
        UserModel userModel = getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
        processExpiredStatus(userModel);
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
        Assert.isTrue(isOldPwdMatch, "bad_credentials");

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
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getStatus(), UserModel.Status.NORMAL)) {
                UserModel updateUser = UserModel.builder().id(userId).status(UserModel.Status.DISABLE).build();
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
    public String batchDisable(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
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
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getStatus(), UserModel.Status.DISABLE)) {
                UserModel updateUser = UserModel.builder().id(userId).status(UserModel.Status.NORMAL).build();
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
    public String batchEnable(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
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
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getStatus(), UserModel.Status.PENDING)) {
                UserModel updateUser = UserModel.builder().id(userId).status(UserModel.Status.NORMAL).build();
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
    public String batchApprove(List<String> userIds) {
        Assert.notEmpty(userIds, "userIds不能为空！");
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
            fastDFSClient.downloadFile(authConfig.getExcelTemplatePath(), authConfig.getExcelTemplateName(), response, FdfsGroupEnum.DEFAULT.toString());
        } catch (MpRuntimeException e) {
            throw new MpRuntimeException("下载模板错误！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(List<ImportUser> list) {
        for (int i = 0; i < list.size(); i++) {
            ImportUser importUser = list.get(i);

            // 1.保存用户信息
            UserModel userModel = importUser2UserModel(importUser);
            try {
                String userId = cn.cyberpeace.mp.base.util.IdGenerator.generateUUID();
                log.warn("userId:{}", userId);
                userModel.setId(userId);
                create(userModel);

            } catch (Exception e) {
                // todo 考虑将错误信息包装返回到页面
                log.error(ExceptionUtil.stacktraceToString(e));
            }

            // 2. 保存用户-角色关系
            String roles = importUser.getRoles().trim();
            Arrays.stream(StringUtils.split(roles, ",")).forEach(ident -> {
                RoleModel roleModel = roleService.getByIdent(ident.trim());
                if (Objects.nonNull(roleModel)) {
                    UserRoleModel userRoleModel = UserRoleModel.builder().user(userModel.getId()).role(roleModel.getId()).build();
                    userRoleService.create(userRoleModel);
                }
            });

            // 3. 保存组织-用户关系
            String orgPaths = importUser.getOrgpath().trim();
            Arrays.stream(StringUtils.split(orgPaths, ",")).forEach(path -> {
                OrgModel orgModel = orgService.getByPath(path);
                if (orgModel != null) {
                    OrgUserModel orgUserModel = OrgUserModel.builder().user(userModel.getId()).org(orgModel.getId()).build();
                    orgUserService.create(orgUserModel);
                }
            });

        }
    }

    @Override
    public List<ExportUser> exportExcel(List<UserModel> users) {
        List<ExportUser> exportUserList = users.stream().map(u -> user2ExportUser(u)).filter(o -> Objects.nonNull(o)).collect(Collectors.toList());
        return exportUserList;
    }


    @Override
    public boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException {
        Assert.notBlank(userId, "userId不能为空！");
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
     * @param user
     */
    public void processExpiredStatus(UserModel user) {
        if (Objects.isNull(user) || Objects.isNull(user.getExpiredTime())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(user.getExpiredTime()) && Objects.equals(user.getStatus(), UserModel.Status.NORMAL)) {
            log.info("用户状态已经过期，准备更新数据库用户状态！ username:{}", user.getUsername());
            user.setStatus(UserModel.Status.EXPIRED);

//            UserModel updateUser = UserModel.builder().id(user.getId()).status(UserModel.Status.EXPIRED).build();
//            update(updateUser);
        }
    }


    private UserModel importUser2UserModel(ImportUser importUser) {
        if (Objects.isNull(importUser)) {
            return null;
        }
        UserModel userModel =
                UserModel.builder().username(importUser.getUsername())
                        .name(importUser.getName())
                        .password(PasswordUtils.getPasswordEncoder().encode(importUser.getPassword()))
                        .build();

        userModel.setIdentifier(importUser.getUsername());

        return userModel;
    }


    private ExportUser user2ExportUser(UserModel user) {
        if (Objects.isNull(user)) {
            return null;
        }
        ExportUser exportUser = ExportUser.builder().username(user.getUsername()).name(user.getName()).build();
        // org path
        List<OrgModel> orgList = orgUserService.listOrgsByUserId(user.getId());
//        List<String> orgList = new ArrayList<>();
//        for (OrgModel org : orgs) {
//            orgList.add(orgService.getPathByOrgId(org.getId()));
//        }

        String paths = orgList.stream().map(o -> orgService.calculatePathByOrgId(o.getId())).collect(Collectors.joining(","));
        log.info("paths:{}", paths);
        exportUser.setOrg(paths);
        exportUser.setStat(user.getStatus() + "");

        // role
        List<RoleModel> roleList = userRoleService.listByUserId(user.getId());
        String roles = roleList.stream().map(r -> r.getIdentifier()).collect(Collectors.joining(","));
        log.info("roles:{}", roles);
        exportUser.setRoles(roles);

        return exportUser;
    }


}
