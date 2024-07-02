package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.response.SysVersionResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysModelVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SysVersionController
 * @Description
 * @Author maojun
 * @Date 2023/8/23 11:21
 * @Version 1.0.0
 */

@Api(value = "系统版本管理接口", tags = "系统版本管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/version")
@AuthTarget(TargetEnum.NO_AUTH)
public class SysVersionController {

    @Autowired
    private SysModelVersionService sysModelVersionService;

    /**
     * 查询系统版本号
     *
     * @return
     */
    @ApiOperation(value = "查询系统版本号", notes = "")
    @GetMapping("/list")
    public ServiceResult<SysVersionResponse> listSysVersion() {
        SysVersionResponse sysVersionResponse = sysModelVersionService.listSysVersion();
        return ResultCommon.success(sysVersionResponse);
    }

}
