package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/7/19 9:25
 */
@Slf4j
@Api(value = "用户变更通知示例")
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH)
public class UserUnauthTestController {

    @PostMapping("/UserApi/ssoAddUser")
    public ServiceResult testAddReport(@RequestBody AddInfo info) {
        log.info("add user info: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/UserApi/ssoModifyUser")
    public ServiceResult testModifyReport(@RequestBody ModifyInfo info) {
        log.info("modify user info: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/UserApi/ssoDelUser")
    public ServiceResult testDeleteReport(@RequestBody DeleteInfo info) {
        log.info("delete user info: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/login/logout")
    public ServiceResult testLogoutReport(@RequestBody LogoutInfo info) {
        log.info("logout user info: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }


    @PostMapping("/test/add")
    public ServiceResult testAddReport2(@RequestBody AddInfo info) {
        log.info("add user info 2: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/test/modify")
    public ServiceResult testModifyReport2(@RequestBody ModifyInfo info) {
        log.info("modify user info 2: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/test/delete")
    public ServiceResult testDeleteReport2(@RequestBody DeleteInfo info) {
        log.info("delete user info 2: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @PostMapping("/test/logout")
    public ServiceResult testLogoutReport2(@RequestBody LogoutInfo info) {
        log.info("logout user info 2: {}", JsonCommon.stringify(info));
        return new ServiceResult(true);
    }

    @Getter
    @Setter
    @ApiModel("用户新增通知")
    public static class AddInfo {
        @ApiModelProperty("动作, 值固定为 add")
        private String action;
        @ApiModelProperty("新增的用户")
        private UserBo data;
    }

    @Getter
    @Setter
    @ApiModel("用户变更通知")
    public static class ModifyInfo {
        @ApiModelProperty("动作, 值固定为 modify")
        private String action;
        @ApiModelProperty("变更的用户")
        private UserBo data;
    }

    @Getter
    @Setter
    @ApiModel("用户变更通知")
    public static class DeleteInfo {
        @ApiModelProperty("动作, 值固定为 delete")
        private String action;
        @ApiModelProperty("删除的用户id列表")
        private List<String> data;
    }

    @Getter
    @Setter
    @ApiModel("用户退出登录通知")
    public static class LogoutInfo {
        @ApiModelProperty("动作, 值固定为 logout")
        private String action;
        @ApiModelProperty("退出登录的用户id")
        private String data;
        private String token;
    }

}
