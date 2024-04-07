package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.request.user.ThirdUserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "第三方用户管理接口", tags = "UNAUTH-第三方用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/users")
@RequiredArgsConstructor
public class ThirdUserController {

    private final UserService userService;

    @ApiOperation("新增/更新第三方用户信息")
    @PostMapping("/upsert")
    public ServiceResult<Boolean> upsertUser(@Validated @RequestBody ThirdUserRequest thirdUserRequest) {
        userService.upsertThirdUser(thirdUserRequest);
        return new ServiceResult<>(Boolean.TRUE).asResult();
    }



}
