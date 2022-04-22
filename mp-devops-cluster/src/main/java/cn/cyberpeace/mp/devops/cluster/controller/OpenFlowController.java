package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.OpenFlowService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.OpenFlowConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.OpenFlowPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.OpenflowActionRequest;
import cn.cyberpeace.mp.devops.cluster.response.OpenflowActionResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import com.google.common.collect.Lists;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private OpenFlowService openFlowService;

    @Autowired
    private OpenFlowConverter openFlowConverter;

    /**
     * 流表操作查询
     * @return
     *
     */
    @ApiOperation(value = "流表操作查询",hidden = true)
    @GetMapping("/action")
    @Deprecated(since = "5.3.3")
    public ServiceResult<OpenflowActionResponse> queryOpenFlowAction(@RequestParam(required = false) String reqHostName) {
        if (StringUtils.isBlank(reqHostName)) {
            reqHostName = hostname;
        }
        OpenflowActionRequest request = OpenflowActionRequest.builder()
                .method(BaseCloudPlatformConstant.OPENFLOW_ACTION_METHOD)
                .hostname(reqHostName)
                .build();
        OpenflowActionResponse response = openFlowService.queryOpenFlowAction(request);

        return ServiceResult.success(response);
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
