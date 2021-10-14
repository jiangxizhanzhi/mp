package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.register.RegisterRequest;
import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.impl.UserCreateCheckService;
import cn.cyberpeace.mp.auth.service.sys.SysConfigService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCreateCheckService createCheckService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private AvatarService avatarService;

    public ObjServiceResult<UserModel> register(RegisterRequest req) {

        UserModel user = UserModel.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .mobile(req.getMobile())
                .email(req.getEmail())
                .nickname(req.getNickname())
                .name(req.getName())
                .avatar(req.getAvatar()).build();

        // 给注册用户分配默认头像
        if (StringUtils.isBlank(user.getAvatar())) {
            List<AvatarModel> avatarModels = avatarService.listAll();
            if (CollectionUtil.isNotEmpty(avatarModels)) {
                user.setAvatar(avatarModels.get(0).getUrl());
            }
        }

        userService.initDefaultAvatar(user);

        // 查看系统设置，注册用户是否需要审核
        if (sysConfigService.isNewUserNeedAudit()) {
            log.info("need registration audit...");
            user.setIsAudited(UserModel.Status.NOT_AUDITED);
        }

        createCheckService.check(user);

        ObjServiceResult<UserModel> result = userService.create(user);
        return result;
    }



}
