package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.register.RegisterRequest;
import cn.cyberpeace.mp.auth.service.impl.UserCreateCheckService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
    private AdminConfigService adminConfigService;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private OrgUserService orgUserService;

    @Transactional(rollbackFor = Exception.class)
    public ObjServiceResult<UserModel> register(RegisterRequest req) {

        UserModel user = UserModel.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .email(req.getEmail())
                .nickname(req.getNickname())
                .name(req.getName())
                .avatar(req.getAvatar()).build();


        // 查看系统设置，注册用户是否需要审核
        if (adminConfigService.isNewUserNeedAudit()) {
            log.info("need registration audit...");
            user.setIsAudited(UserModel.Status.NOT_AUDITED);
        }

        createCheckService.check(user);

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
        ResultUtils.throwExceptionIfError(re, AuthErrorCode.USER_ORG_ADD_ERROR);

        userCacheService.evictAllUserCache();
        return result;
    }



}
