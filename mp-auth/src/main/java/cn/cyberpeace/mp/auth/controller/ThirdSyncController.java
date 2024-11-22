package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.request.user.*;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/mp/unauth")
public class ThirdSyncController {

    @Resource
    private OrgMapper orgMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @PostMapping("/api/v1/user")
    public Res syncUser(@RequestBody ThirdSyncUser user) {
        log.info("sync user , {}", JsonCommon.stringify(user));
        UserRequest request = new UserRequest();
        request.setIdentifier(user.getAccountNo());
        request.setMobile(user.getMobile());
        request.setTenant("default");
        request.setUsername(user.getAccountNo());
        request.setPassword("");
        request.setTokenSecret("");
        request.setName(user.getUserName());
        request.setNickname(user.getUserName());
        request.setAvatar("");
        request.setEmail(user.getEmail());
        request.setDescription("");
        request.setName(user.getUserUid());
        List<String> orgIds = new ArrayList<>();
        List<String> roleIds = new ArrayList<>();
        request.setOrgIds(orgIds);
        request.setRoleIds(roleIds);
        if (user.getOrgs() != null) {
            for (ThirdSyncUserOrganization r : user.getOrgs()) {
                orgIds.add(r.getOrgCustomUnique());
            }
        }
        if (user.getUserTypes() != null) {
            for (ThirdSyncUserType t : user.getUserTypes()) {
                roleIds.add(t.getTypeCode());
            }
        }

        HumanCondition condition = new HumanCondition().filterEqual(UserModel::getIdentifier, request.getIdentifier());
        var userModel = userMapper.first(condition);
        if (userModel == null) {
            userService.add(request);
        } else {
            userService.modify(request);
        }
        return Res.success();
    }

    @PostMapping("/api/v1/org")
    public Res syncOrg(@RequestBody ThirdSyncOrg org) {
        log.info("sync user , {}", JsonCommon.stringify(org));

        OrgModel orgModel = new OrgModel();
        orgModel.setDescription("");
        orgModel.setTenant("default");
        orgModel.setId(org.getOrgCustomUnique());
        orgModel.setParent(org.getParentOrgId());
        orgModel.setName(org.getOrgName());
        orgModel.setIdentifier(org.getOrgCustomUnique());

        boolean existOrg = orgMapper.get(orgModel.getId()) != null;
        if (existOrg) {
            orgMapper.update(orgModel);
        } else {
            orgMapper.insert(orgModel);
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
    }
}
