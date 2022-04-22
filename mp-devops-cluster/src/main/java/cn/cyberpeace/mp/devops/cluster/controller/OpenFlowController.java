package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.OpenFlowService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.OpenFlowConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.OpenFlowPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 流表监控类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/openflow")
@Api(tags = "流表监控")
public class OpenFlowController {

    @Value("${mp.devops.hostname}")
    private String hostname;

    private OpenFlowService openFlowService;

    private OpenFlowConverter openFlowConverter;

    public OpenFlowController(OpenFlowService openFlowService, OpenFlowConverter openFlowConverter) {
        this.openFlowService = openFlowService;
        this.openFlowConverter = openFlowConverter;
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public PageResult<OpenFlowVO> page(OpenFlowPageRequest request){
        OpenFlowPageRequest openFlowPageRequest = new OpenFlowPageRequest();
        openFlowPageRequest.setPage(request.getPage());
        openFlowPageRequest.setPerPage(request.getPerPage());
        OpenFlowPageConverterModel openFlowPageConverterModel = openFlowConverter.page(openFlowPageRequest);
        if (Objects.isNull(openFlowPageConverterModel)){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        return new PageResult<>(openFlowPageConverterModel.getOpenFlowVOS(),openFlowPageConverterModel.getTotal());
    }

}
