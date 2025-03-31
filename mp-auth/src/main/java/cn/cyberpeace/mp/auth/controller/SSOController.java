package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.sso.SSOConfigBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.sso.SSOLoginRequest;
import cn.cyberpeace.mp.auth.service.SSOService;
import cn.cyberpeace.mp.auth.service.SSOServiceFactory;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.controller
 * @Author: hhuang
 * @CreateTime: 2025/03/13  15:04
 * @Description: 第三方单点登录管理接口
 * @Version: 1.0
 */
@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/sso")
@AuthTarget(TargetEnum.NO_AUTH)
public class SSOController {

    @Resource
    private SSOServiceFactory ssoServiceFactory;

    @Resource
    private SSOConfig ssoConfig;


    @Resource
    private UserService userService;

    @ApiOperation(value = "拦截测试", notes = "拦截")
    @GetMapping("/logouts")
    public void logouts(HttpServletRequest request, HttpServletResponse response) {
        log.info("sso logout ...");
        UserModel user = JwtCommon.verifyToken(request);
        if (user != null) {
            var updateUser = new UserModel();
            updateUser.setId(user.getId());
            updateUser.initDefault("tokenSecret");
            userService.update(updateUser);
        }
    }

    @ApiOperation(value = "登出跳转url获取", notes = "登出跳转url获取")
    @GetMapping("/logout/redirect")
    public ServiceResult<String> logoutRedirect(@RequestParam(value = "out_user_type") String outUserType,
                                                @RequestParam(value = "account_id") String accountId,
                                                @RequestParam(value = "four_aticket") String fourATicket,
                                                HttpServletRequest request, HttpServletResponse response) {
        String url = "";
        // 当前登录人员的平台类型
        log.info("local user type:" + outUserType + "; local user accountId:" + accountId + "; local user ticket:" + fourATicket);
        if (accountId != null && outUserType != null) {
            if (outUserType.equals(SSOSysEnum.FOUR_A.getCode())) {
                // 4a平台用户登出
                SSOLoginOutBO ssoLoginOutBO = SSOLoginOutBO.builder()
                        .ticket(fourATicket)
                        .request(request)
                        .response(response)
                        .build();
                SSOService ssoService = ssoServiceFactory.getSSOService(SSOSysEnum.FOUR_A);
                ssoService.logout(ssoLoginOutBO);
                url = ssoConfig.getOutHomePageUrl();
                return ResultCommon.success(url).asResult();
            } else if (outUserType.equals(SSOSysEnum.SOC.getCode())) {
                return ResultCommon.success(url).asResult();
            }
        }
        return ResultCommon.success(url).asResult();
    }

    /**
     * @param ssoLoginRequest
     * @param request
     * @param response
     * @return void
     * @throws Exception
     * @description: 4a单点登录
     */
    @ApiOperation(value = "4a单点登录", notes = "4a单点登录")
    @GetMapping("/login/4a")
    public void ssoFourALogin(@Validated SSOLoginRequest ssoLoginRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("4a login,ssoLoginRequest: {}", JSON.toJSONString(ssoLoginRequest));
        SSOLoginBO ssoLoginBO = SSOLoginBO.builder()
                .ticket(ssoLoginRequest.getTicket())
                .request(request)
                .response(response)
                .build();
        SSOService ssoService = ssoServiceFactory.getSSOService(SSOSysEnum.FOUR_A);
        ssoService.login(ssoLoginBO);
    }

    /**
     * @param ssoLoginRequest
     * @param request
     * @param response
     * @return void
     * @throws Exception
     * @description: soc单点
     */
    @ApiOperation(value = "soc单点登录", notes = "soc单点登录")
    @GetMapping("/login/soc")
    public void ssoSocLogin(@Validated SSOLoginRequest ssoLoginRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("soc login,ssoLoginRequest: {}", JSON.toJSONString(ssoLoginRequest));
        SSOLoginBO ssoLoginBO = SSOLoginBO.builder()
                .ticket(ssoLoginRequest.getTicket())
                .service(ssoLoginRequest.getService())
                .request(request)
                .response(response)
                .build();
        SSOService ssoService = ssoServiceFactory.getSSOService(SSOSysEnum.SOC);
        ssoService.login(ssoLoginBO);
    }


    @ApiOperation("单点登录相关配置查询")
    @GetMapping("/get_sso_config")
    public ServiceResult<SSOConfigBO> getThirdLoginConfig() {
        SSOConfigBO ssoConfigBO = SSOConfigBO.builder().defaultRedirectUrl(ssoConfig.getDefaultRedirectUrl()).build();
        return new ServiceResult<>(ssoConfigBO).asResult();
    }
}
