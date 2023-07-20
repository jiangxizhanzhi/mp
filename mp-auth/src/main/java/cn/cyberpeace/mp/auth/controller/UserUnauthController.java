package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserSimpleBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/7/19 9:25
 */
@Api(value = "用户相关接口（无鉴权）", tags = "用户相关接口（无鉴权）")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/users")
public class UserUnauthController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "查询组织下所有用户")
    @ApiImplicitParam(name = "org", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/query")
    public ObjServiceResult<List<UserSimpleBo>> query(@RequestParam String org) {
        log.info("UserUnauthController.query begin. org:{}", org);
        List<UserBo> userBos = userService.getUserDtoByOrg(org);
        List<UserSimpleBo> userSimpleBos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userBos)) {
            for (UserBo userBo : userBos) {
                UserSimpleBo userSimpleBo = new UserSimpleBo();
                BeanUtils.copyProperties(userBo, userSimpleBo);
                userSimpleBos.add(userSimpleBo);
            }
        }
        return ResultUtils.obj2ObjServiceResult(userSimpleBos);
    }

}
