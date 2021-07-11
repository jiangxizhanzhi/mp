package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.request.SuricataQueryRequest;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Api(value = "流量分析规则", tags = "流量分析规则")
@Slf4j
@RestController
@RequestMapping(SuricataConstant.BASE_API_PATH)
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

}
