 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowController.java.
 // File size 1962.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.OpenFlowConverter;
import cn.cyberpeace.mp.devopscluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devopscluster.dto.OpenFlowPageDTO;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;
import cn.cyberpeace.mp.devopscluster.vo.OpenFlowVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OpenFlowController
 * @Description 流表监控类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/openflow")
@Api(tags = "流表监控")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NETWORK_MANAGEMENT)
public class OpenFlowController {

    private final OpenFlowConverter openFlowConverter;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<OpenFlowVO> page(BasePageRequest request) {
        OpenFlowPageDTO dto = new OpenFlowPageDTO();
        dto.setOffset(request.getPage());
        dto.setLimit(request.getPerPage());
        OpenFlowPageConverterModel openFlowPageConverterModel = openFlowConverter.page(dto);
        return new PageResult<>(openFlowPageConverterModel.getOpenFlowVOS(), openFlowPageConverterModel.getTotal());
    }

}
