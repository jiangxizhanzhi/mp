package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.config.SSOConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.sso.SSOLoginRequest;
import cn.cyberpeace.mp.auth.service.SSOService;
import cn.cyberpeace.mp.auth.service.SSOServiceFactory;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/sso")
@AuthTarget(TargetEnum.NO_AUTH)
public class SSOController {

    @Resource
    private SSOServiceFactory ssoServiceFactory;

    @Resource
    private SSOConfig ssoConfig;

    @ApiOperation(value = "拦截", notes = "拦截")
    @GetMapping("/logouts")
    public void logouts(){
        log.info("test!!!!!!!!!!!!!!!!!!!!!!");
    }

    @ApiOperation(value = "获取登出跳转url", notes = "获取登出跳转url(需要token)")
    @GetMapping("/logout/redirect")
    public ObjServiceResult logoutRedirect(HttpServletRequest request, HttpServletResponse response){
        String url = "";
        // 查询当前人员
        UserModel loginUser = UserUtils.getLoginUser();
        if (loginUser.getAccountId() != null && loginUser.getOutUserType() != null) {
            if (loginUser.getOutUserType().equals(SSOSysEnum.FOUR_A.getCode())) {
                // 4a平台用户登出
                SSOLoginOutBO ssoLoginOutBO = SSOLoginOutBO.builder()
                        .ticket(UserUtils.getLoginUser().getFourATicket())
                        .request(null)
                        .response(null)
                        .build();
                SSOService ssoService = ssoServiceFactory.getSSOService(SSOSysEnum.FOUR_A);
                ssoService.logout(ssoLoginOutBO);
                // 返回登出跳转url
                url = ssoConfig.getOutHomePageUrl();
            } else {
//                url = ssoConfig.getHomePageUrl();
            }
        }
        return new ObjServiceResult<>(new ObjResult<>(url)).asResult();
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
}
