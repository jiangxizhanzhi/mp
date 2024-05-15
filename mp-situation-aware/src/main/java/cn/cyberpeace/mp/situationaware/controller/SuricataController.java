package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonConstant;
import cn.cyberpeace.mp.situationaware.bo.SuricataData;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.request.SuricataQueryRequest;
import cn.cyberpeace.mp.situationaware.request.SuricataRuleRequest;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.controller
 * @Author: hhuang
 * @CreateTime: 2023/04/21  09:25
 * @Description: Arkime使用的Suricata规则控制器
 * @Version: 1.0
 */
@Api(value = "流量分析规则", tags = "流量分析规则")
@Slf4j
@RestController
@RequestMapping(SuricataConstant.BASE_API_PATH)
@AuthTarget(TargetEnum.TRAFFIC_ANALYSE_LIST)
public class SuricataController {

    @Resource
    private SuricataService suricataService;


    @PostMapping("/rule/reimport")
    @PreAuthorize("hasAnyAuthority('ROLE_" + SuricataConstant.ROLE_ADMIN + "')")
    public ServiceResult importSuricataRules(MultipartFile file) {
        return ResultCommon.success(suricataService.handlerRuleFile(file));
    }


    @ApiOperation(value = "分页查询流量分析规则")
    @GetMapping("/rule/page")
    public ServiceResult querySuricataRulesPage(SuricataQueryRequest suricataQueryRequest){
        return suricataService.queryPage(suricataQueryRequest);
    }

    @ApiOperation(value = "查询流量分析规则所有协议")
    @GetMapping("/rule/protocols")
    public ServiceResult querySuricataRulesAllProtocols(){
        return suricataService.queryAllProtocols();
    }

    @ApiOperation(value = "查询流量分析规则所有的分类类别")
    @GetMapping("/rule/classtype")
    public ServiceResult<List<String>> listAllClassTypes() {
        return new ServiceResult<>(suricataService.queryAllClassType()).asResult();
    }

    @ApiOperation(value = "解析用户输入的规则")
    @PostMapping("/rule/analyse")
    public ServiceResult<SuricataData> analyseSuricataRule(@RequestBody SuricataRuleRequest suricataRuleRequest) {
        return new ServiceResult<>(suricataService.analyseRule(suricataRuleRequest.getMetadata())).asResult();
    }

    @ApiOperation(value = "保存用户编辑的规则")
    @PostMapping("/rule/save")
    public ServiceResult<Boolean> saveSuricataRule(@RequestBody SuricataRuleRequest suricataRuleRequest) {
        suricataService.saveRule(suricataRuleRequest);
        return new ServiceResult<>(Boolean.TRUE).asResult();
    }

    @ApiOperation(value = "获取流量规则文件存储路径")
    @GetMapping("/rule/file_path")
    @AuthTarget(TargetEnum.NO_AUTH)
    public ServiceResult<String> getSuricataRuleFilePath() {
        return new ServiceResult<>(LocalFileMappingCommon.getMapping(CommonConstant.SURICATA_RULE_GROUP, CommonConstant.SURICATA_RULE_FILE_NAME)
                .getRemoteFile()).asResult();
    }

}
