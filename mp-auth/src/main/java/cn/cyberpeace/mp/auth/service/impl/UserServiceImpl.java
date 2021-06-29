package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ServletUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
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
    public UserModel getByUserName(String userName) {
        log.info("userName:{}", userName);
        return getModelMapper().first(new HumanCondition().filterEqual("username", userName));
    }

    @Override
    public UserModel getByMobile(String mobile) {
        log.info("mobile:{}", mobile);
        return getModelMapper().first(new HumanCondition().filterEqual("mobile", mobile));
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


        UserModel userUpdate = new UserModel();
        userUpdate.setId(currentUser.getId());
        userUpdate.setPassword(newPwd);
        update(userUpdate);
        return true;
    }

    @Override
    public boolean resetPwd(String userId, String newPwd) {
        // 密码是否合法
        Assert.isFalse(PasswordUtils.isPasswordStrengthLowLevel(newPwd), AuthConstant.ERROR_CODE_PREFIX + "password_strength_invalid");

        var source = get(userId).getSource();
        UserModel user = source == null ? null : source.getObj();
        Assert.notNull(user, AuthConstant.ERROR_CODE_PREFIX + "account_not_existed");

        UserModel userUpdate = new UserModel();
        userUpdate.setId(user.getId());
        userUpdate.setPassword(newPwd);
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
                UserModel updateUser = new UserModel();
                updateUser.setId(userId);
                updateUser.setStatus(UserModel.Status.DISABLE);
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
    public boolean enable(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getStatus(), UserModel.Status.DISABLE)) {
                UserModel updateUser = new UserModel();
                updateUser.setId(userId);
                updateUser.setStatus(UserModel.Status.NORMAL);
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
    public boolean approve(String userId) {
        Assert.notBlank(userId, "userId不能为空！");
        Optional<UserModel> userOptional = Optional.ofNullable(getModelMapper().get(userId));
        if (userOptional.isPresent()) {
            UserModel userModel = userOptional.get();
            if (Objects.equals(userModel.getStatus(), UserModel.Status.PENDING)) {
                UserModel updateUser = new UserModel();
                updateUser.setId(userId);
                updateUser.setStatus(UserModel.Status.NORMAL);
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
        status = status == null ? UserModel.Status.NORMAL : status;
        humanCondition.filterEqual("status", status);
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
    public void importExcel(List<ImportUser> list) {
        for (int i = 0; i < list.size(); i++) {
            ImportUser importUser = list.get(i);

            // 1.保存用户信息
            UserModel userModel = importUser2User(importUser);
            try {
                // todo 考虑将错误信息包装返回到页面
                create(userModel);
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
            }

            // 2. 保存用户-角色关系
            String roles = importUser.getRoles().trim();
            String[] roleNameArr = roles.split(",");
            for (String roleName : roleNameArr) {
            }

            // 3. 保存组织-用户关系
        }
    }

    @Override
    public void exportExcel() {

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

                UserModel updateUser = new UserModel();
                updateUser.setId(userId);
                updateUser.setAvatar(httpUrl);
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


    private UserModel importUser2User(ImportUser importUser) {
        if (importUser == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        userModel.setUsername(importUser.getUsername());
        userModel.setName(importUser.getName());
        userModel.setPassword(PasswordUtils.getPasswordEncoder().encode(importUser.getPassword()));

        userModel.setIdentifier(importUser.getUsername());

        return userModel;
    }

}
