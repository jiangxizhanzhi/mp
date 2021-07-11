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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
public class UploadDataListener extends AnalysisEventListener<ImportUserBo> {

    
    private static final int BATCH_COUNT = 100;

    List<ImportUserBo> list = new ArrayList<ImportUserBo>();
    
    private UserService userService;

    private RoleService roleService;

    private OrgService orgService;

    private OperateUserPermissionService opUserPermCheckService;

    private AuthConfig authConfig;

    
    public UploadDataListener(UserService userService, RoleService roleService, OrgService orgService,
                              OperateUserPermissionService opUserPermCheckService, AuthConfig authConfig) {
        this.userService = userService;
        this.roleService = roleService;
        this.orgService = orgService;
        this.opUserPermCheckService = opUserPermCheckService;
        this.authConfig = authConfig;
    }

    
    @Override
    public void invoke(ImportUserBo data, AnalysisContext context) {
        list.add(data);
    }

    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        
        saveData();
        log.info("all data saved success！");
    }

    
    private void saveData() {
        if (list.size() > authConfig.getMaxImportUserSize()) {
            log.error("size exceed the limit:{}！", list.size());
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format(authConfig.getMaxImportUserSize().toString()));
        }

        
        checkImportUsers(list);
        preProcessOrgsAndRoles(list);
        preProcessPwd(list);
        userService.importUsers(list);
    }



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

        
        var duplicateElements = getDuplicateElements(users);
        if (CollectionUtils.isNotEmpty(duplicateElements)) {
            String duplicatedUserNames = duplicateElements.stream().map(Map.Entry::getKey).collect(Collectors.joining(","));
            log.error("import error! username {} duplicated!", duplicatedUserNames);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_DUPLICATED.format(duplicatedUserNames));
        }

        
        checkUserNameFromDB(userName2IndexBoMap);
    }

    
    private void checkDescription(String description, int index) {
        if (StringUtils.isNotBlank(description) && StringUtils.length(description) > 1000) {
            String shortDesc = description.substring(0, 20) + "...";
            log.error("No {} row error! description {} has illegal length!", index, shortDesc);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_DESC_ILLEGAL_LENGTH.format(String.valueOf(index), shortDesc));
        }
    }



    
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

    
    private void checkUserNameFromDB(Map<String, Integer> userName2IndexBoMap) {
        List<String> userNames = userName2IndexBoMap.keySet().stream().collect(Collectors.toList());
        List<String> existedUserNames = userService.listExistedUserNames(userNames, 1);
        if (CollectionUtils.isNotEmpty(existedUserNames)) {
            String username = existedUserNames.get(0);
            log.error("No {} row error! username {} already existed!", userName2IndexBoMap.get(username), username);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_USERNAME_EXISTED.format(String.valueOf(userName2IndexBoMap.get(username)), username));
        }
    }

    
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


    
    private void checkPwd(String password, int index) {
        if (StringUtils.isBlank(password)) {
            log.error("No {} row error! name {} should not be empty!", index, password);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_PWD_EMPTY.format(String.valueOf(index)));
        }
        
        if (PasswordUtils.isPasswordInvalid(password)) {
            log.error("No {} row error! password has illegal strength!", index);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_PWD_INVALID.format(String.valueOf(index)));
        }
    }

    
    private void checkExpiredTime(String expiredTime, int index) {
        
        if (StringUtils.isNotBlank(expiredTime)) {
            try {
                TimeUtils.stringToLocalDate(expiredTime);
            } catch (Exception e) {
                log.error("No.{0} row error! expired time [{1}] format not correct!", index, expiredTime);
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_EXPIRED_TIME_INVALID.format(String.valueOf(index), expiredTime));
            }
        }
    }


    
    private void checkRole(ImportUserBo user, int index) {
        String roles = user.getRoles();
        if (StringUtils.isBlank(roles)) {
            log.error("No {} row error! roles:{} is empty! ", index, roles);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_EMPTY.format(String.valueOf(index)));
        }

        
        user.setRoleIdList(new ArrayList<>());
        String[] roleNameArr = StringUtils.split(roles, ",");

        
            opUserPermCheckService.checkByTargetRoleNames(Arrays.asList(roleNameArr));
        
        
        
        
        

        for (String roleName : roleNameArr) {
            RoleModel roleModel = roleService.getByName(roleName.trim());

            if (Objects.isNull(roleModel)) {
                log.error("No {} row error! role {} not existed!", index, roleName.trim());
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_ROLE_NOT_EXISTED.format(String.valueOf(index), roleName.trim()));
            }
            user.getRoleIdList().add(roleModel.getId());
        }
    }

    
    private void checkOrgs(ImportUserBo user, int index) {
        String orgPaths = user.getOrgpath();
        if (StringUtils.isBlank(orgPaths)) {
            
            
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


    private void preProcessOrgsAndRoles(List<ImportUserBo> users) {
        preProcessOrgs(users);
        preProcessRoles(users);
    }


    
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


    private List<ImportUserBo> enPwd(List<ImportUserBo> list) {
        list.forEach(u -> {
            u.setPassword(PasswordUtils.encode(u.getPassword()));
        });
        return list;
    }


    
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

        try {
            opUserPermCheckService.checkByTargetRoleNames(allRoleNames);
        } catch (MpRuntimeException mpe) {
            if (Objects.nonNull(mpe.getServiceCode()) && Objects.equals(mpe.getServiceCode().getCode(), AuthErrorCode.PERMISSION_DENIED.getCode())) {
                throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_IMPORT_ROLE_LIMIT);
            }
        }
    }


    public List<Map.Entry<String, Long>> getDuplicateElements(List<ImportUserBo> users) {
        var list = users.stream().collect(Collectors.groupingBy(u -> u.getUsername(), Collectors.counting())).entrySet().stream().filter(entry -> entry.getValue() > 1).collect(Collectors.toList());
        return list;
    }


}
