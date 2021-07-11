package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
@Api(tags = "日志模块")
public class OperateLogController {

    private OperateLogService operateLogService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @Autowired
    public OperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation(value = "新增操作日志")
    @ApiImplicitParam(name = "request", value = "操作日志请求", dataTypeClass = OperationLogRequest.class)
    @PostMapping("/add")
    public ServiceResult add(@RequestBody OperationLogRequest request, HttpServletRequest httpServletRequest){
        request.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        operateLogService.create(request);
        return ResultCommon.success();
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/page")
    public ListObjServiceResult<OperationLogPageResponse> page(OperationLogPageRequest request) {
        request.ensure();
        return operateLogService.search(request);
    }

    @GetMapping("/modules")
    @ApiOperation(value = "查询审计日志操作模块")
    public ServiceResult<List<OptionBO>> getModules() {
        return optionService.getIntOptions(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_MODULE);
    }

    @GetMapping("/types")
    @ApiOperation(value = "查询审计日志操作类型")
    public ServiceResult<List<OptionBO>> getTypes() {
        return optionService.getIntOptions(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_TYPE);
    }

}
