package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthBizConfig;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 模板的读取类
 * 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UploadDataListener extends AnalysisEventListener<ImportUser> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    List<ImportUser> list = new ArrayList<ImportUser>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private UserService userService;

    private RoleService roleService;

    private OrgService orgService;

    private OperateUserPermissionService opUserPermCheckService;

    private AuthBizConfig authBizConfig;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param userService
     */
    public UploadDataListener(UserService userService, RoleService roleService, OrgService orgService,
                              OperateUserPermissionService opUserPermCheckService, AuthBizConfig authBizConfig) {
        this.userService = userService;
        this.roleService = roleService;
        this.orgService = orgService;
        this.opUserPermCheckService = opUserPermCheckService;
        this.authBizConfig = authBizConfig;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ImportUser data, AnalysisContext context) {
        list.add(data);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("all data saved success！");
    }

    /**
     * 加上存储数据库
     * http://10.10.10.10/biz/bug-view-4861.html
     *
     * 当前导入模板里面说明的必填项是用户名、姓名、密码， 其他的组织、角色、电话号码没有在导入模板中说必填，
     * 组织不填的话导入后要有默认值。
     * 组织：可以不填， 不填的话默认根组织
     * 角色：必填， 属于已有角色可以导入，不属于已有角色导入失败。
     */
    private void saveData() {
        if (list.size() > authBizConfig.getMaxImportUserSize()) {
            log.error("size exceed the limit:{}！", list.size());
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format(authBizConfig.getMaxImportUserSize().toString()));
        }

        // 校验字段是否合法
        checkImportUsers(list);
        preProcessImportUsers(list);
        userService.importUsers(list);
    }



    private void checkImportUsers(List<ImportUser> users) {
        if (CollectionUtil.isEmpty(users)) {
            throw new MpRuntimeException(AuthErrorCode.EXCEL_USER_LIST_EMPTY);
        }

        for (int i = 0; i < users.size(); i++) {
            ImportUser user = users.get(i);
            int index = i + 1;
            if (Objects.nonNull(user)) {
                checkUserName(user.getUsername(), index);
                checkName(user.getName(), index);
                checkPwd(user.getPassword(), index);
            }
        }
    }


    /**
     * 校验用户名
     *
     * @param username
     * @param index
     */
    private void checkUserName(String username, int index) {
        if (StringUtils.isBlank(username)) {
            log.error("No {} row error! username {} should not be empty!", index, username);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_EMPTY.format(String.valueOf(index)));
        }

        if (StringUtils.length(username) < 2 || StringUtils.length(username) > 20) {
            log.error("No {} row error! username {} has illegal length!", index, username);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_ILLEGAL_LENGTH.format(String.valueOf(index), username));
        }

        if (Objects.nonNull(userService.getByUserName(username))) {
            log.error("No {} row error! username {} already existed!", index, username);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_EXISTED.format(String.valueOf(index), username));
        }

    }

    /**
     * 校验名称
     *
     * @param name
     * @param index
     */
    private void checkName(String name, int index) {
        if (StringUtils.isBlank(name)) {
            log.error("No {} row error! name {} should not be empty!", index, name);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_NAME_EMPTY.format(String.valueOf(index)));
        }

        if (StringUtils.length(name) < 2 || StringUtils.length(name) > 40) {
            log.error("No {} row error! name {} has illegal length!", index, name);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_NAME_ILLEGAL_LENGTH.format(String.valueOf(index), name));
        }

    }


    /**
     * 校验密码
     *
     * @param password
     * @param index
     */
    private void checkPwd(String password, int index) {
        // 密码是否合法
        if (PasswordUtils.isPasswordStrengthLowLevel(password)) {
            log.error("No {} row error! password {} has illegal strength!", index, password);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_PWD_INVALID.format(String.valueOf(index), password));
        }
    }


    /**
     * 校验角色是否存在，如果存在则查出id留待后面绑定的时候用
     *
     * @param
     * @param index
     */
    private void checkRole(ImportUser user, int index) {
        String roles = user.getRoles();
        if (StringUtils.isBlank(roles)) {
            log.error("No {} row error! roles:{} is empty! ", index, roles);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_EMPTY.format(String.valueOf(index)));
        }

        // 清空roleIdList
        user.setRoleIdList(new ArrayList<>());
        String[] roleNameArr = StringUtils.split(roles, ",");

        //try {
            opUserPermCheckService.checkByTargetRoleNames(Arrays.asList(roleNameArr));
        //} catch (MpRuntimeException me) {
        //    if (me.getServiceCode().equals(AuthErrorCode.PERMISSION_DENIED)) {
        //        throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_NOT_EXISTED.format(String.valueOf(index), roleName.trim()));
        //    }
        //}

        for (String roleName : roleNameArr) {
            RoleModel roleModel = roleService.getByName(roleName.trim());

            if (Objects.isNull(roleModel)) {
                log.error("No {} row error! role {} not existed!", index, roleName.trim());
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_NOT_EXISTED.format(String.valueOf(index), roleName.trim()));
            }
            user.getRoleIdList().add(roleModel.getId());
        }
    }

    /**
     * 校验组织是否存在，如果存在则查出id留待后面绑定的时候用
     *
     * @param
     * @param index
     */
    private void checkOrgs(ImportUser user, int index) {
        String orgPaths = user.getOrgpath();
        if (StringUtils.isBlank(orgPaths)) {
            // 组织路径不存在，默认都存一级组织
            // 组织为空则默认一级组织
            List<OrgModel> orgs = orgService.listParents();
            OrgModel defaultTopOrg = orgs.get(0);
            if (Objects.isNull(defaultTopOrg)) {
                log.error("默认一级组织不存在！");
                throw new MpRuntimeException(BaseErrorCode.ERROR, "default org not existed!");
            }

            user.setOrgIdList(CollectionUtil.toList(defaultTopOrg.getId()));
            return;
        }

        user.setOrgIdList(new ArrayList<>());
        Arrays.stream(StringUtils.split(orgPaths, ",")).forEach(path -> {
            OrgModel orgModel = orgService.getByPath(path.trim());
            if (Objects.isNull(orgModel)) {
                log.error("No {} row error! org {} not existed!", index, path.trim());
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ORG_NOT_EXISTED.format(String.valueOf(index), path.trim()));
            } else {
                user.getOrgIdList().add(orgModel.getId());
            }
        });
    }


    private void preProcessImportUsers(List<ImportUser> users) {
        preProcessPwd(users);
        preProcessOrgs(users);
        preProcessRoles(users);
    }


    /**
     * 预处理密码
     */
    private void preProcessPwd(List<ImportUser> users) {
        Map<String, String> pwdMap = new HashMap<>();
        for (ImportUser user : users) {
            if (StringUtils.isEmpty(pwdMap.get(user.getPassword()))) {
                pwdMap.put(user.getPassword(), PasswordUtils.encode(user.getPassword().trim()));
            }
            user.setPassword(pwdMap.get(user.getPassword()));
        }
    }


    /**
     * 预处理组织
     */
    private void preProcessOrgs(List<ImportUser> users) {
        Map<String, OrgModel> orgPath2ModelMap = new HashMap<>();
        List<OrgModel> orgs = orgService.listParents();
        OrgModel defaultTopOrg = orgs.get(0);
        if (Objects.isNull(defaultTopOrg)) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, "default org not existed!");
        }

        for (int i = 0; i < users.size(); i++) {
            ImportUser user = users.get(i);
            int index = i + 1;
            String orgPaths = user.getOrgpath();
            if (StringUtils.isBlank(orgPaths)) {
                // 组织路径不存在，默认都存一级组织
                // 组织为空则默认一级组织
                user.setOrgIdList(CollectionUtil.toList(defaultTopOrg.getId()));
                continue;
            }

            user.setOrgIdList(new ArrayList<>());
            Arrays.stream(StringUtils.split(orgPaths, ",")).forEach(path -> {
                OrgModel orgModel = orgPath2ModelMap.get(path.trim());
                if (Objects.isNull(orgModel)) {
                    orgModel = orgService.getByPath(path.trim());
                    orgPath2ModelMap.put(path.trim(), orgModel);
                }
                if (Objects.isNull(orgModel)) {
                    log.error("No {} row error! org {} not existed!", index, path.trim());
                    throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ORG_NOT_EXISTED.format(String.valueOf(index), path.trim()));
                } else {
                    user.getOrgIdList().add(orgModel.getId());
                }
            });
        }
    }

    private void preProcessRoles(List<ImportUser> users) {
        List<String> allRoleNames = new ArrayList<>();
        Map<String, RoleModel> roleName2ModelMap = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            ImportUser user = users.get(i);
            int index = i + 1;

            String roles = user.getRoles();
            if (StringUtils.isBlank(roles)) {
                log.error("No {} row error! roles:{} is empty! ", index, roles);
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_EMPTY.format(String.valueOf(index)));
            }

            // 清空roleIdList
            user.setRoleIdList(new ArrayList<>());
            String[] roleNameArr = StringUtils.split(roles, ",");
            allRoleNames.addAll(Arrays.asList(roleNameArr));

            for (String roleName : roleNameArr) {
                RoleModel roleModel = roleName2ModelMap.get(roleName.trim());
                if (Objects.isNull(roleModel)) {
                    roleModel = roleService.getByName(roleName.trim());
                    roleName2ModelMap.put(roleName.trim(), roleModel);
                }

                if (Objects.isNull(roleModel)) {
                    log.error("No {} row error! role {} not existed!", index, roleName.trim());
                    throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_NOT_EXISTED.format(String.valueOf(index), roleName.trim()));
                }
                user.getRoleIdList().add(roleModel.getId());
            }
        }

        opUserPermCheckService.checkByTargetRoleNames(allRoleNames);
    }


}
