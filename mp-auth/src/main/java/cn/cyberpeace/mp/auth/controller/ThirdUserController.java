package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "第三方用户管理接口", tags = "UNAUTH-第三方用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/users")
@RequiredArgsConstructor
public class ThirdUserController {

    private final UserService userService;



}
