package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 系统配置管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "配置管理接口", tags = "AUTH-配置管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/config")
@UnlimitPagination("list")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @ApiOperation(value = "查询所有系统设置", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<SysConfigModel>> listAll() {
        List<SysConfigModel> configs = sysConfigService.listAll();
        return ResultCommon.success(configs);
    }

}
