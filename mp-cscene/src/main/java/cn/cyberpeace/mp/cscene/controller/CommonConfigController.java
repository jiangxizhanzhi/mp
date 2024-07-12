 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonConfigController.java.
 // File size 1557.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName CommonConfigController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "场景通用配置", tags = "CSCENE-场景通用配置")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/config")
@Data
@AuthTarget(TargetEnum.NO_AUTH)
public class CommonConfigController {

    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    /**
     * 查询靶标对应的探针系统分类
     *
     * @return
     */
    @ApiOperation("查询靶标对应的探针系统分类")
    @GetMapping("/system_type_to_agent_support")
    public ServiceResult<Map<String, Object>> getSystemTypeToAgentSupport() {
        return cloudCommonConfigService.getSystemTypeToAgentSupport().asResult();
    }

}
