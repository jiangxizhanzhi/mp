package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CommonConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Api(value = "场景通用配置", tags = "CSCENE-场景通用配置")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/config")
@Data
public class CommonConfigController {

    @Resource
    private CommonConfigService commonConfigService;

    @ApiOperation("查询靶标对应的探针系统分类")
    @GetMapping("/system_type_to_agent_support")
    public ServiceResult<Map<String, Object>> getSystemTypeToAgentSupport() {
        return commonConfigService.getSystemTypeToAgentSupport().asResult();
    }

}
