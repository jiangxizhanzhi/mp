 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UploadDataListener.java.
 // File size 18383.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.excel.ImportUserBo;
import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

 /**
  * 上传数据监听器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class UploadDataListener extends AnalysisEventListener<ImportUserBo> {

     /**
      * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
      */
     private static final int BATCH_COUNT = 100;

     /**
      * 列表
      */
     List<ImportUserBo> list = new ArrayList<ImportUserBo>();
     /**
      * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
      */
     private UserService userService;

     /**
      * 角色服务
      */
     private RoleService roleService;

     /**
      * org服务
      */
     private OrgService orgService;

     /**
      * 人事处用户烫检查服务
      */
     private OperateUserPermissionService opUserPermCheckService;

     /**
      * 身份验证配置
      */
     private AuthConfig authConfig;

     /**
      * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
      *
      * @param userService            用户服务
      * @param roleService            角色服务
      * @param orgService             org服务
      * @param opUserPermCheckService 人事处用户烫检查服务
      * @param authConfig             身份验证配置
      */
     public UploadDataListener(UserService userService, RoleService roleService, OrgService orgService,
                              OperateUserPermissionService opUserPermCheckService, AuthConfig authConfig) {
        this.userService = userService;
        this.roleService = roleService;
        this.orgService = orgService;
        this.opUserPermCheckService = opUserPermCheckService;
        this.authConfig = authConfig;
    }

     /**
      * 这个每一条数据解析都会来调用
      *
      * @param data    one row value. Is is same as
      * @param context 上下文
      */
     @Override
    public void invoke(ImportUserBo data, AnalysisContext context) {
        list.add(data);
    }

     /**
      * 所有数据解析完成了 都会来调用
      *
      * @param context 上下文
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
      * 当前导入模板里面说明的必填项是用户名、姓名、密码， 其他的组织、角色、电话号码没有在导入模板中说必填，
      * 组织不填的话导入后要有默认值。
      * 组织：可以不填， 不填的话默认根组织
      * 角色：必填， 属于已有角色可以导入，不属于已有角色导入失败。
      */
     private void saveData() {
        if (list.size() > authConfig.getMaxImportUserSize()) {
            log.error("size exceed the limit:{}！", list.size());
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format(authConfig.getMaxImportUserSize().toString()));
        }

        // 校验字段是否合法
        checkImportUsers(list);
        preProcessOrgsAndRoles(list);
        preProcessPwd(list);
        userService.importUsers(list);
    }

     /**
      * 检查导入用户
      *
      * @param users 用户
      */
     private void checkImportUsers(List<ImportUserBo> users) {
        if (CollectionUtil.isEmpty(users)) {
            throw new MpRuntimeException(AuthErrorCode.EXCEL_USER_LIST_EMPTY);
        }

        Map<String, Integer> userName2IndexBoMap = new LinkedHashMap<>();
        for (int i = 0; i < users.size(); i++) {
            ImportUserBo user = users.get(i);
            int index = i + 1;
            if (Objects.nonNull(user)) {
                userName2IndexBoMap.put(user.getUsername(), index);
                checkUserName(user.getUsername(), index);
                checkName(user.getName(), index);
                checkPwd(user.getPassword(), index);
                checkExpiredTime(user.getExpiredTime(), index);
                checkDescription(user.getDescription(), index);
            }
        }

        // 表格内用户名重复内容校验
        var duplicateElements = getDuplicateElements(users);
        if (CollectionUtils.isNotEmpty(duplicateElements)) {
            String duplicatedUserNames = duplicateElements.stream().map(Map.Entry::getKey).collect(Collectors.joining(","));
            log.error("import error! username {} duplicated!", duplicatedUserNames);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_DUPLICATED.format(duplicatedUserNames));
        }

        // 校验用户名
        checkUserNameFromDB(userName2IndexBoMap);
    }

     /**
      * 校验简介是否合法
      *
      * @param description
      * @param index       指数
      */
     private void checkDescription(String description, int index) {
        if (StringUtils.isNotBlank(description) && StringUtils.length(description) > 1000) {
            String shortDesc = description.substring(0, 20) + "...";
            log.error("No {} row error! description {} has illegal length!", index, shortDesc);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_DESC_ILLEGAL_LENGTH.format(String.valueOf(index), shortDesc));
        }
    }

     /**
      * 校验用户名
      *
      * @param username 用户名
      * @param index    指数
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
    }

     /**
      * 校验用户名
      *
      * @param userName2IndexBoMap 用户名2索引bo映射
      */
     private void checkUserNameFromDB(Map<String, Integer> userName2IndexBoMap) {
        List<String> userNames = userName2IndexBoMap.keySet().stream().collect(Collectors.toList());
        List<String> existedUserNames = userService.listExistedUserNames(userNames, 1);
        if (CollectionUtils.isNotEmpty(existedUserNames)) {
            String username = existedUserNames.get(0);
            log.error("No {} row error! username {} already existed!", userName2IndexBoMap.get(username), username);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_EXISTED.format(String.valueOf(userName2IndexBoMap.get(username)), username));
        }
    }

     /**
      * 校验名称
      *
      * @param name
      * @param index 指数
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
      * @param index    指数
      */
     private void checkPwd(String password, int index) {
        if (StringUtils.isBlank(password)) {
            log.error("No {} row error! name {} should not be empty!", index, password);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_PWD_EMPTY.format(String.valueOf(index)));
        }
        // 密码是否合法
        if (PasswordUtils.isPasswordInvalid(password)) {
            log.error("No {} row error! password has illegal strength!", index);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_PWD_INVALID.format(String.valueOf(index)));
        }
    }

     /**
      * 校验过期日期格式
      *
      * @param expiredTime
      * @param index       指数
      */
     private void checkExpiredTime(String expiredTime, int index) {
        // 过期时间格式是否合法
        if (StringUtils.isNotBlank(expiredTime)) {
            try {
                final LocalDate localDate = TimeUtils.stringToLocalDate(expiredTime);
            } catch (Exception e) {
                log.error("No.{0} row error! expired time [{1}] format not correct!", index, expiredTime);
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_EXPIRED_TIME_INVALID.format(String.valueOf(index), expiredTime));
            }
        }
    }

     /**
      * 校验角色是否存在，如果存在则查出id留待后面绑定的时候用
      *
      * @param user  用户
      * @param index 指数
      */
     private void checkRole(ImportUserBo user, int index) {
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
      * @param user  用户
      * @param index 指数
      */
     private void checkOrgs(ImportUserBo user, int index) {
        String orgPaths = user.getOrgpath();
        if (StringUtils.isBlank(orgPaths)) {
            // 组织路径不存在，默认都存一级组织
            // 组织为空则默认一级组织
            List<OrgBO> orgBOS = orgService.listParents();
            OrgBO defaultTopOrg = orgBOS.get(0);
            if (Objects.isNull(defaultTopOrg)) {
                log.error("默认一级组织不存在！");
                throw new MpRuntimeException(BaseErrorCode.ERROR, "default org not existed!");
            }

            user.setOrgIdList(CollectionUtil.toList(defaultTopOrg.getId()));
            return;
        }

        user.setOrgIdList(new ArrayList<>());
        Arrays.stream(StringUtils.split(orgPaths, ",")).forEach(path -> {
            OrgBO orgBO = orgService.getByPath(path.trim());
            if (Objects.isNull(orgBO)) {
                log.error("No {} row error! org {} not existed!", index, path.trim());
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ORG_NOT_EXISTED.format(String.valueOf(index), path.trim()));
            } else {
                user.getOrgIdList().add(orgBO.getId());
            }
        });
    }

     /**
      * @param users 用户
      */
     private void preProcessOrgsAndRoles(List<ImportUserBo> users) {
        preProcessOrgs(users);
        preProcessRoles(users);
    }

     /**
      * 导入excel对密码进行预处理
      * 预处理密码
      *
      * @param users 用户
      */
     private void preProcessPwd(List<ImportUserBo> users) {
        List<List<ImportUserBo>> parentList = Lists.partition(users, 20);
        List<CompletableFuture<List<ImportUserBo>>> cfList = new ArrayList<>();

        for (List<ImportUserBo> subList : parentList) {
            CompletableFuture<List<ImportUserBo>> cf = CompletableFuture.supplyAsync(() -> enPwd(subList));
            cfList.add(cf);
        }

        List<ImportUserBo> finalList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(cfList)) {
            CompletableFuture[] cfArray = cfList.toArray(new CompletableFuture[0]);
            CompletableFuture<List<List<ImportUserBo>>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> cfList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            List<List<ImportUserBo>> result = finalCF.join();
            result.forEach(list -> finalList.addAll(list));
        }
    }

     /**
      * @param list 列表
      * @return {@link List }<{@link ImportUserBo }>
      */
     private List<ImportUserBo> enPwd(List<ImportUserBo> list) {
        list.forEach(u -> {
            u.setPassword(PasswordUtils.encode(u.getPassword()));
        });
        return list;
    }

     /**
      * 预处理组织
      *
      * @param users 用户
      */
     private void preProcessOrgs(List<ImportUserBo> users) {
        Map<String, OrgBO> orgPath2ModelMap = new HashMap<>();
        List<OrgBO> orgBOS = orgService.listParents();
        OrgBO defaultTopOrg = orgBOS.get(0);
        if (Objects.isNull(defaultTopOrg)) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, "default org not existed!");
        }

        for (int i = 0; i < users.size(); i++) {
            ImportUserBo user = users.get(i);
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
                OrgBO orgBO = orgPath2ModelMap.get(path.trim());
                if (Objects.isNull(orgBO)) {
                    orgBO = orgService.getByPath(path.trim());
                    orgPath2ModelMap.put(path.trim(), orgBO);
                }
                if (Objects.isNull(orgBO)) {
                    log.error("No {} row error! org {} not existed!", index, path.trim());
                    throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ORG_NOT_EXISTED.format(String.valueOf(index), path.trim()));
                } else {
                    user.getOrgIdList().add(orgBO.getId());
                }
            });
        }
    }

     /**
      * @param users 用户
      */
     private void preProcessRoles(List<ImportUserBo> users) {
        List<String> allRoleNames = new ArrayList<>();
        Map<String, RoleModel> roleName2ModelMap = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            ImportUserBo user = users.get(i);
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
                    // roleService.getByName方法有缓存
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

        try {
            opUserPermCheckService.checkByTargetRoleNames(allRoleNames);
        } catch (MpRuntimeException mpe) {
            if (Objects.nonNull(mpe.getServiceCode()) && Objects.equals(mpe.getServiceCode().getCode(), AuthErrorCode.PERMISSION_DENIED.getCode())) {
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_IMPORT_ROLE_LIMIT);
            }
        }
    }

     /**
      * @param users 用户
      * @return {@link List }<{@link Map.Entry }<{@link String }, {@link Long }>>
      */
     public List<Map.Entry<String, Long>> getDuplicateElements(List<ImportUserBo> users) {
        var list = users.stream().collect(Collectors.groupingBy(u -> u.getUsername(), Collectors.counting())).entrySet().stream().filter(entry -> entry.getValue() > 1).collect(Collectors.toList());
        return list;
    }


}
