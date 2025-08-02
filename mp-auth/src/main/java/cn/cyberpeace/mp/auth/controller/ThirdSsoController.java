package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.ThirdLoginConfig;
import cn.cyberpeace.mp.auth.bo.user.ThirdUser;
import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.impl.UserServiceImpl;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;


    @AuthTarget(TargetEnum.NO_AUTH)
    @GetMapping
    @ApiOperation("赛宁第三方登录接口")
    public String loginAndRedirectToHomePage(@RequestParam(value = "token") String token, HttpServletRequest request, HttpServletResponse response) {
        log.info("第三方登录,token: {}");
        if (token == null) {
            String url = thirdSsoConfig.getThirdLoginPageUrl();
            log.info("第三方登录, token 为空 , 重定向至 {}", thirdSsoConfig.getThirdLoginPageUrl());
            return String.format("redirect:" + url);
        } else {
            // 在第三方校验token有效性
            String responseBody = null;
            ThirdUser thirdUser = null;
            try {
                String tokenCheckUrl = thirdSsoConfig.getTicketCheckUrl();
                log.info("校验 url:{}", thirdSsoConfig.getTicketCheckUrl());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
                HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(tokenCheckUrl, HttpMethod.GET, requestEntity, String.class);
                responseBody = responseEntity.getBody();
                log.info("返回内容: {}", responseBody);
                thirdUser = JSON.parseObject(responseBody).getJSONObject("data").toJavaObject(ThirdUser.class);
            } catch (Exception e) {
                log.info("failed to check token , response is {}", responseBody, e);
                throw new RuntimeException(e);
            }

            String userName = thirdUser.getUsername();

            HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, userName);
            UserModel userModel = userMapper.first(condition);
            if (Objects.isNull(userModel)) {
                log.info("用户不存在 , 需要新建用户 , username is {}", userName);
                userModel = this.createUser(thirdUser, token);
            } else {
                this.updateUser(userModel, token);
            }

//            更新菜单权限
            List<RoleModel> roleModelList = userRoleService.listByUserId(userModel.getId());
            if(CollectionUtils.isNotEmpty(roleModelList)){
                // 清除原来所有用户关联的角色
                userRoleService.deleteByUserId(userModel.getId());
                List<String> roleIdList = roleModelList.stream().map(RoleModel::getId).collect(Collectors.toList());
                roleService.bulkDelete(roleIdList.toArray(new String[0]));
            }
            List<String> perms = JSON.parseArray(thirdUser.getPerms(), String.class);
            RoleRequest roleRequest = new RoleRequest();
            roleRequest.setName(userName);
            roleRequest.setTargetIds(perms);
            RoleModel roleModel = roleService.addV3(roleRequest);
            userService.saveUserRoleRelationV3(roleModel.getId(), userModel.getId());

            String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
            response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
            response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");

            return "redirect:" + thirdSsoConfig.getLoginSuccessRedirectUrl();
        }
    }

    private UserModel createUser(ThirdUser thirdUser, String token) {
        UserRequest newUserReq = new UserRequest();
        newUserReq.setMobile(thirdUser.getPhoneNumber());
        newUserReq.setTenant("default");
        newUserReq.setUsername(thirdUser.getUsername());
        newUserReq.setPassword(String.format("%s123#", thirdUser.getId()));
        newUserReq.setName(thirdUser.getRealName());
        newUserReq.setNickname(thirdUser.getUsername());
        newUserReq.setAvatar("");
        newUserReq.setEmail(thirdUser.getEmail());
        newUserReq.setDescription("");

        RoleMapper roleMapper = BeanCommon.getBean(RoleMapper.class);
        /**
         * 无锡移动的 超级管理员 管理员 -> 管理员 ， 全部前后台菜单
         * 无锡移动的 省管理 专家 -> 教员 ， 全部前后台菜单（不包含后台系统管理、系统运维菜单）
         * 无锡移动的 学员 -> 学员，全部前台菜单
         */
//        if ("soc_supper_admin".equals(thirdUser.getRoleCode()) || "sys_admin".equals(thirdUser.getRoleCode())) {
//            var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "管理员"));
//            newUserReq.setRoleIds(Arrays.asList(role.getId()));
//        } else if ("soc_admin".equals(thirdUser.getRoleCode()) || "soc_expert".equals(thirdUser.getRoleCode())) {
//            var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "教员"));
//            newUserReq.setRoleIds(Arrays.asList(role.getId()));
//        } else {
//            var role = roleMapper.first(new HumanCondition().filterEqual(RoleModel::getName, "学员"));
//            newUserReq.setRoleIds(Arrays.asList(role.getId()));
//        }

        newUserReq.setToken(token);
        userService.addV2(newUserReq);

        HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, newUserReq.getUsername());
        return userMapper.first(condition);
    }

    private void updateUser(UserModel userModel, String token) {
        HumanCondition condition = new HumanCondition().filterEqual(UserModel::getUsername, userModel.getUsername());
        UserModel update = new UserModel();
        update.setToken(token);
        userMapper.filterUpdate(update, condition);
    }


    @AuthTarget(TargetEnum.NO_AUTH)
    @GetMapping("/get_third_login_config")
    @ApiOperation("第三方登录配置查询")
    @ResponseBody
    public ServiceResult<ThirdLoginConfig> getThirdLoginConfig() {
        ThirdLoginConfig thirdLoginConfig = new ThirdLoginConfig();
        thirdLoginConfig.setThirdLoginPageUrl(this.thirdSsoConfig.getThirdLoginPageUrl());
        return new ServiceResult<>(thirdLoginConfig).asResult();
    }

}
