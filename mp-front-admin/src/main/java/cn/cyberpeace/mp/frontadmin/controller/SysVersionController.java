package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysModelVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/8/23 11:21
 */

@Api(value = "系统版本管理接口", tags = "系统版本管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/version")
public class SysVersionController {

    @Autowired
    private SysModelVersionService sysModelVersionService;

    @ApiOperation(value = "查询系统版本号", notes = "")
    @GetMapping("/list")
    public ServiceResult<List<SysModelVersionModel>> listSysVersion() {
        List<SysModelVersionModel> rtnList = sysModelVersionService.listSysVersion();
        return ResultCommon.success(rtnList);
    }

}
