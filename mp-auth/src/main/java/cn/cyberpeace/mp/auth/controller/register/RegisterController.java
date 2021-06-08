package cn.cyberpeace.mp.auth.controller.register;


import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.register.RegisterReq;
import cn.cyberpeace.mp.auth.service.RegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "注册接口", tags = "RegisterController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;


    @ApiOperation(value = "获取注册短信", notes = "")
    @GetMapping("/sms")
    public void register(@RequestParam String mobile) {
        log.info("mobile:{}", mobile);



    }

    @ApiOperation(value = "注册接口", notes = "")
    @PostMapping
    public void register(@RequestBody RegisterReq registerReq) {
        log.info("registerReq:{}", registerReq);

        registerService.register(registerReq);

    }

}
