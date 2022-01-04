package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.login.LoginResp;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final ObjectMapper mapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    private UserService userService;

    @Autowired
    public CustomAuthenticationSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        UserModel user = null;
        ServiceResult<Object> result;

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (Objects.isNull(user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = new ServiceResult<>(null, BaseErrorCode.ERROR);
        } else {
            recordLoginLog(user, request);
            AuthErrorCode errorCode = checkUserErrorStatus(user);
            if (Objects.nonNull(errorCode)) {
                response.setStatus(HttpServletResponse.SC_OK);
                result = new ServiceResult<>(null, errorCode);
            } else {
                String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(user.getId(), user.getUsername(), user.getTokenSecret()));
                ObjResult<UserModel> userResult = new ObjResult(user, new UserFields().getAvailableFields());
                UserDto userDto = userService.userModel2UserDto(userResult.getObj());
                LoginResp loginResp = new LoginResp();
                loginResp.setUser(userDto);
                loginResp.setToken(jwt);

                // 角色
//                List<RoleModel> roles = userRoleService.listByUserId(user.getId());
//                loginResp.setRoles(roles);

                // 权限 暂时不用返回
                /*
                List<TargetModel> targets = userPermissionService.listByUserId(user.getId());
                loginResp.setTargets(targets);
                */

                // 菜单 暂时不用返回
                /*
                List<TargetModel> menuTargets = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(targets)) {
                    menuTargets = targets.stream().filter(t -> Objects.equals(t.getType(), TargetModel.TYPE_MENU)).collect(Collectors.toList());
                }
                loginResp.setMenus(menuTargets);
                */

                // 组织
//                List<OrgDto> orgDtos = orgUserService.listHierarchicalOrgsByUserId(user.getId());
//                loginResp.setOrgs(orgDtos);

                result = ServiceResult.builder().source(loginResp).build();

                // 首次登陆，需要修改密码
                if (user.getInitCheck() == UserModel.INT_CHECK_YES) {
                    result = ServiceResult.builder().source(loginResp).serviceCode(AuthErrorCode.NEED_UPDATE_CREDENTIALS).build();
                }
            }
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }


    private AuthErrorCode checkUserErrorStatus(UserModel user) {
        String errorStatusMsg = null;
        AuthErrorCode errorCode = null;

        // 状态优先级 禁用 > 过期 > 待审核
        if (Objects.equals(user.getIsAudited(), UserModel.Status.NOT_AUDITED)) {
            errorStatusMsg = "用户未审核，不能登录！";
            errorCode = AuthErrorCode.ACCOUNT_NOT_AUDITED;
        }

        // 过期
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            errorStatusMsg = "用户已过期，不能登录！";
            errorCode = AuthErrorCode.ACCOUNT_EXPIRED;
        }

        // 禁用
        if (Objects.equals(user.getIsEnabled(), UserModel.Status.DISABLED)) {
            errorStatusMsg = "用户已被禁用，不能登录！";
            errorCode = AuthErrorCode.ACCOUNT_DISABLED;
        }

        log.info("Login blocked! {}", errorStatusMsg);
        return errorCode;
    }


    private void recordLoginLog(UserModel user, HttpServletRequest request) {
        OperationLogRequest logModel = new OperationLogRequest();
        //操作类型
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpType(OpTypeEnums.LOGIN.ordinal());
        logModel.setOpTarget(MessageCommon.get(OpTargetEnums.USER.getCode()));
        //操作主题
        logModel.setTitle(MessageCommon.get(AuthBasei18nCode.LOGIN));
        //操作描述
        logModel.setDescription(MessageCommon.get(AuthBasei18nCode.LOGIN_USERNAME_PASSWORD));

        //操作用户
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        logModel.setUserType(LogConstant.USER_TYPE_USER);

        logModel.setOpIp(SnUtils.getRealRemoteIp(request));

        // 记录登录日志
        operateLogServiceFactory.create(logModel, LocaleContextHolder.getLocale());
    }


}
