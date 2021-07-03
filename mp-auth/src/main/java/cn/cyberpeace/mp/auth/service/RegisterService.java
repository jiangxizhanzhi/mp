package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.register.RegisterRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserCreateCheckService checkService;

    public ObjServiceResult<UserModel> register(RegisterRequest req) {

        UserModel user = UserModel.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .mobile(req.getMobile())
                .email(req.getEmail())
                .nickname(req.getNickname())
                .name(req.getName())
                .avatar(req.getAvatar()).build();

        // 查看系统设置，注册用户是否需要审批
        if (authConfig.isNeedRegistrationAudit()) {
            log.info("need registration audit...");
            user.setStatus(UserModel.Status.PENDING);
        }

        checkService.check(user);

        ObjServiceResult<UserModel> result = userService.create(user);
        return result;
    }



}
