package cn.cyberpeace.mp.front.admin.business.controller;

import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.business.service.SysConfigCheckService;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
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

    @Autowired
    private SysConfigCheckService checkService;


    @ApiOperation(value = "查询所有系统设置", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<SysConfigModel>> getall() {
        List<SysConfigModel> configs = sysConfigService.listAll();
        return ServiceResult.success(configs);
    }

    @ApiOperation(value = "根据keyname更新单条sysconfig", notes = "")
    @PostMapping("/update/bykeyname")
    public ServiceResult updateByKeyName(@RequestBody SysConfigModel updateConfig) {

        checkService.updateCheck(Arrays.asList(updateConfig));

        boolean success = sysConfigService.updateByKeyName(updateConfig);
        if (success) {
            return ServiceResult.successful();
        } else {
            return ServiceResult.fail("更新系统配置失败!");
        }
    }

    @ApiOperation(value = "根据keyname批量更新sysconfig", notes = "")
    @PostMapping("/updateall")
    public ServiceResult updateAll(@RequestBody @NotEmpty List<SysConfigModel> configs) {
        // 更新规则，如果key存在，就修改value值，如果key不存在，就忽略不改
        checkService.updateCheck(configs);

        boolean success = sysConfigService.batchUpdateByKeyname(configs);
        if (success) {
            return ServiceResult.successful();
        } else {
            return ServiceResult.fail("更新系统配置失败!");
        }
    }

}
