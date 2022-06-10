package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.register.RegisterRequest;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.impl.UserCreateCheckService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserCreateCheckService createCheckService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private OrgUserService orgUserService;

    public ObjServiceResult<UserModel> register(RegisterRequest req) {

        UserModel user = UserModel.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .mobile(req.getMobile())
                .email(req.getEmail())
                .nickname(req.getNickname())
                .name(req.getName())
                .avatar(req.getAvatar()).build();

        // 查看系统设置，注册用户是否需要审核
        if (sysConfigService.isNewUserNeedAudit()) {
            log.info("need registration audit...");
            user.setIsAudited(UserModel.Status.NOT_AUDITED);
        }

        createCheckService.check(user);

        userService.chooseDefaultAvatar(user);

        ObjServiceResult<UserModel> result = userService.create(user);

        String userId = "";
        if (Objects.nonNull(result.getSource().getObj())) {
            UserModel userModel = result.getSource().getObj();
            userId = userModel.getId();
        } else {
            log.error("create user error!");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        OrgUserModel orgUserModel = OrgUserModel.builder().user(userId).org(AuthConstant.ROOT_ORG_ID).build();
        var re = orgUserService.create(orgUserModel);
        ResultUtils.throwExceptionIfError(re, "create org user error!");

        return result;
    }



}
