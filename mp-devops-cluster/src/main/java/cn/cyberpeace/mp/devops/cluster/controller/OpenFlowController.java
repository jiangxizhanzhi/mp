package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.OpenFlowConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.dto.OpenFlowPageDTO;
import cn.cyberpeace.mp.devops.cluster.request.BasePageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流表监控类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/openflow")
@Api(tags = "流表监控")
@RequiredArgsConstructor
public class OpenFlowController {

    private final OpenFlowConverter openFlowConverter;

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
