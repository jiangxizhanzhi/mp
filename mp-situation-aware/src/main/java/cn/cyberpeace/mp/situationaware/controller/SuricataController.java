package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.request.SuricataQueryRequest;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincollection.controller
 * @Author: hhuang
 * @CreateTime: 2023/04/21  09:25
 * @Description: Arkime使用的Suricata规则控制器
 * @Version: 1.0
 */
@Api(value = "流量分析规则", tags = "流量分析规则")
@Slf4j
@RestController
@RequestMapping(SuricataConstant.BASE_API_PATH)
public class SuricataController {

    @Resource
    private SuricataService suricataService;


//    @PostMapping("/rule/reimport")
//    @PreAuthorize("hasAnyAuthority('ROLE_" + SuricataConstant.ROLE_ADMIN + "')")
//    public ServiceResult importSuricataRules(MultipartFile file) {
//        return ResultCommon.success(suricataService.handlerRuleFile(file));
//    }


    @ApiOperation(value = "分页查询流量分析规则")
    @GetMapping("/rule/page")
    public ServiceResult querySuricataRulesPage(SuricataQueryRequest suricataQueryRequest){
        return suricataService.queryPage(suricataQueryRequest);
    }


}
