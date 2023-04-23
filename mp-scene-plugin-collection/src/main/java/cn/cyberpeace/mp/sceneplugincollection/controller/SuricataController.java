package cn.cyberpeace.mp.sceneplugincollection.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.constant.SuricataConstant;
import cn.cyberpeace.mp.sceneplugincollection.service.SuricataService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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


    @PostMapping("/rule/reimport")
    @PreAuthorize("hasAnyAuthority('ROLE_" + SuricataConstant.ROLE_ADMIN + "')")
    public ServiceResult importSuricataRules(MultipartFile file) {
        boolean b = suricataService.handlerRuleFile(file);
        return ResultCommon.success(b);
    }

}
