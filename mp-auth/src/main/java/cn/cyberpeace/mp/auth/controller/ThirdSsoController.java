package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.ThirdLoginConfig;
import cn.cyberpeace.mp.auth.bo.user.ThirdUser;
import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.impl.UserServiceImpl;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

@Api(value = "第三方登录管理接口", tags = "UNAUTH-第三方登录接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/third-login")
public class ThirdSsoController {

    @Resource
    private ThirdSsoConfig thirdSsoConfig;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private UserMapper userMapper;

    @GetMapping
    @ApiOperation("赛宁第三方登录接口")
    public String loginAndRedirectToHomePage(@RequestParam(value = "ticket") String ticket, HttpServletRequest request, HttpServletResponse response) {
        if (ticket == null) {
            String url = thirdSsoConfig.getThirdLoginPageUrl();
            log.info("第三方登录, ticket 为空 , 重定向至 {}", thirdSsoConfig.getThirdLoginPageUrl());
            return String.format("redirect:" + url);
        } else {
            // 在第三方校验ticket有效性
            String responseBody = null;
            XmlMapper xmlMapper = new XmlMapper();
            ThirdUser thirdUser = null;
            try {
                responseBody = restTemplate.getForObject(thirdSsoConfig.getTicketCheckUrl(), String.class);
                thirdUser = xmlMapper.readValue(responseBody, new TypeReference<ThirdUser>() {
                });
            } catch (Exception e) {
                log.info("failed to check ticket , response is {}", responseBody, e);
                throw new RuntimeException(e);
            }

            var thirdUserParams = thirdUser.getAuthenticationSuccess().getAttributes();
            String userName = thirdUserParams.getUserId();

            HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, userName);
            UserModel userModel = userMapper.first(condition);
            if (Objects.isNull(userModel)) {
                log.info("用户不存在 , 需要新建用户 , username is {}", userName);
                userModel = this.createUser(thirdUserParams, ticket);
            } else {
                this.updateUser(userModel, ticket);
            }

            String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
            response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
            response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");

            return "redirect:" + thirdSsoConfig.getLoginSuccessRedirectUrl();
        }
    }

    private UserModel createUser(ThirdUser.Attributes thirdUserParams, String ticket) {
        UserRequest newUserReq = new UserRequest();
        newUserReq.setMobile(thirdUserParams.getPhone());
        newUserReq.setTenant("default");
        newUserReq.setUsername(thirdUserParams.getUserId());
        newUserReq.setPassword(String.format("%s123#", thirdUserParams.getUserId()));
        newUserReq.setName(thirdUserParams.getRealName());
        newUserReq.setNickname(thirdUserParams.getUsername());
        newUserReq.setAvatar("");
        newUserReq.setEmail(thirdUserParams.getEmail());
        newUserReq.setDescription("");
        newUserReq.setRoleIds(Arrays.asList("12312312"));
        newUserReq.setOrgIds(Arrays.asList("asdfasdf"));
        newUserReq.setTicket(ticket);
        userService.addV2(newUserReq);

        HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, newUserReq.getUsername());
        return userMapper.first(condition);
    }

    private void updateUser(UserModel userModel, String ticket) {
        HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, userModel.getUsername());
        UserModel update = new UserModel();
        update.setTicket(ticket);
        userMapper.filterUpdate(update, condition);
    }


    @GetMapping("/get_third_login_config")
    @ApiOperation("第三方登录配置查询")
    @ResponseBody
    public ServiceResult<ThirdLoginConfig> getThirdLoginConfig() {
        ThirdLoginConfig thirdLoginConfig = BeanCommon.getBean(ThirdLoginConfig.class);
        thirdLoginConfig.setThirdLoginPageUrl(thirdLoginConfig.getThirdLoginPageUrl());
        return new ServiceResult<>(thirdLoginConfig).asResult();
    }

}
