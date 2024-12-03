package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.request.user.ThirdSyncOrg;
import cn.cyberpeace.mp.auth.request.user.ThirdSyncUser;
import cn.cyberpeace.mp.auth.request.user.ThirdSyncUserOrganization;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.impl.TenantUserServiceImpl;
import cn.cyberpeace.mp.auth.service.impl.UserServiceImpl;
import cn.cyberpeace.mp.auth.util.ThirdSsoUtil;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/unauth")
public class ThirdSyncController {

    @Resource
    private OrgMapper orgMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private TenantUserServiceImpl tenantUserService;

    @Resource
    private ThirdSsoConfig config;

    @AuthTarget(TargetEnum.NO_AUTH)
    @PostMapping("/api/v1/user")
    public Res syncUser(@RequestBody Map<String, Object> input, @RequestParam(value = "access_token") String accessToken) {
        log.info("同步用户, 内容: {}", JsonCommon.stringify(input));
        ThirdSsoUtil.verify(accessToken, config.getClientsecret());
        ThirdSyncUser user = JsonCommon.parse(JsonCommon.stringify(input), ThirdSyncUser.class);
        UserRequest request = new UserRequest();
        request.setThirdAccountNo(user.getAccountNo());
        request.setMobile(user.getMobile());
        request.setTenant("default");
        request.setUsername(user.getWesisCustomUnique());
        request.setPassword(String.format("%s123#", user.getWesisCustomUnique()));
        request.setName(user.getUserName());
        request.setNickname(user.getUserName());
        request.setAvatar("");
        request.setEmail(user.getEmail());
        request.setDescription("");
        List<String> orgIds = new ArrayList<>();
        List<String> roleIds = new ArrayList<>();
        request.setRoleIds(roleIds);
        request.setOrgIds(orgIds);
        if (user.getOrgs() != null) {
            for (ThirdSyncUserOrganization r : user.getOrgs()) {
                orgIds.add(r.getOrgCustomUnique());
            }
        }
        if (user.getRoles() != null) {
            for (var t : user.getRoles()) {
                roleIds.add(t.getRole_code());
            }
        }


        if (user.getActionFlag() == 4) {
            // 禁用
            HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, request.getUsername());
            var userModel = userMapper.first(condition);
            if (userModel != null) {
                userService.disable(userModel.getId());
            } else {
                log.error("禁用用户，但是指定的用户不存在!");
                return Res.fail("disable user , user not exists");
            }
        } else if (user.getActionFlag() == 3) {
            // 启用
            HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, request.getUsername());
            var userModel = userMapper.first(condition);
            if (userModel != null) {
                userService.enable(userModel.getId());
            } else {
                log.error("启用用户，但是指定的用户不存在!");
                return Res.fail("enable user , user not exists");
            }
        } else {
            HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, request.getUsername());
            var userModel = userMapper.first(condition);
            if (userModel == null) {
                // 新增用户
                log.info("新增用户");
                userService.addV2(request);
            } else {
                // 修改用户
                log.info("修改用户 , user id {} ", userModel.getId());
                request.setId(userModel.getId());
                userService.modifyV2(request);
            }
        }
        return Res.success();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @PostMapping("/api/v1/org")
    public Res syncOrg(@RequestBody Map<String, Object> input, @RequestParam(value = "access_token") String accessToken) {
        log.info("同步组织, 内容: {}", JsonCommon.stringify(input));
        ThirdSyncOrg org = JsonCommon.parse(JsonCommon.stringify(input), ThirdSyncOrg.class);
        if (org.getActionFlag() == 4) {
            orgMapper.delete(org.getOrgCustomUnique());
        } else {
            OrgModel orgModel = new OrgModel();
            orgModel.setDescription("");
            orgModel.setTenant("default");
            orgModel.setId(org.getIdt_org__id());
            orgModel.setParent(org.getParentOrgID());
            orgModel.setName(org.getOrgName());
            orgModel.setIdentifier(org.getOrgCustomUnique());
            orgModel.initDefault();

            boolean existOrg = orgMapper.get(orgModel.getId()) != null;
            if (existOrg) {
                orgMapper.update(orgModel);
            } else {
                orgMapper.insert(orgModel);
            }
        }

        return Res.success();
    }

    @Getter
    @Setter
    public static class Res {
        private String code;
        private String msg;

        public static Res success() {
            Res res = new Res();
            res.setCode("0");
            return res;
        }

        public static Res fail(String msg) {
            Res res = new Res();
            res.setCode("-1");
            res.setMsg(msg);
            return res;
        }
    }
}
