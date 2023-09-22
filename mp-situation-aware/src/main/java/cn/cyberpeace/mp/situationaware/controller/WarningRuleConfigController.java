package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigParams;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigQuery;
import cn.cyberpeace.mp.situationaware.service.WarningRuleConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName WarningRuleConfigController
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 9:16
 **/
@Api(value = "告警规则配置", tags = "告警规则配置")
@Slf4j
@RestController
@RequestMapping(SuricataConstant.BASE_API_PATH + "/warning-rule")
public class WarningRuleConfigController {

    @Autowired
    private WarningRuleConfigService warningRuleConfigService;

    @GetMapping("/page")
    @ApiOperation(value = "告警规则分页查询")
    public ListObjServiceResult<WarningRuleConfigDto> pageWarningRuleConfig(WarningRuleConfigParams warningRuleConfigParams) {
        return warningRuleConfigService.pageWarningRuleConfig(warningRuleConfigParams).asResult();
    }

    @GetMapping
    @ApiOperation(value = "查询指定id的告警规则")
    public ServiceResult<WarningRuleConfigDto> getWarningRuleConfigById(@RequestParam String id) {
        return new ServiceResult<>(warningRuleConfigService.getWarningRuleConfigById(id));
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新告警规则信息")
    public ServiceResult<Void> update(@RequestBody WarningRuleConfigDto warningRuleConfigDto) {
        warningRuleConfigService.updateWarningRuleConfig(warningRuleConfigDto);
        return new ServiceResult<>(null);
    }


    @PostMapping("/create")
    @ApiOperation(value = "新建告警规则信息")
    public ServiceResult<Void> create(@RequestBody WarningRuleConfigDto warningRuleConfigDto) {
        warningRuleConfigService.saveWarningRuleConfig(warningRuleConfigDto);
        return new ServiceResult<>(null);
    }




}
