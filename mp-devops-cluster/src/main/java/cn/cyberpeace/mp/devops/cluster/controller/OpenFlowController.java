package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.OpenFlowConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.OpenFlowPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
public class OpenFlowController {

    private OpenFlowConverter openFlowConverter;

    public OpenFlowController(OpenFlowConverter openFlowConverter) {
        this.openFlowConverter = openFlowConverter;
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<OpenFlowVO> page(OpenFlowPageRequest request) {
        OpenFlowPageRequest openFlowPageRequest = new OpenFlowPageRequest();
        openFlowPageRequest.setPage(request.getPage());
        openFlowPageRequest.setPerPage(request.getPerPage());
        OpenFlowPageConverterModel openFlowPageConverterModel = openFlowConverter.page(openFlowPageRequest);
        return new PageResult<>(openFlowPageConverterModel.getOpenFlowVOS(), openFlowPageConverterModel.getTotal());
    }

}
