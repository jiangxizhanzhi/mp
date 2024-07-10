package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.exportHandle.ExportHandle;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.request.LogsExportrequest;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import cn.cyberpeace.mp.log.request.LogExportRequest;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户操作日志记录
 *
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
@Api(tags = "日志模块")
public class OperateLogController {

    private OperateLogService operateLogService;

    @Autowired
    private ExportHandle exportHandle;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @Autowired
    public OperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation(value = "新增操作日志")
    @ApiImplicitParam(name = "request", value = "操作日志请求", dataTypeClass = OperationLogRequest.class)
    @PostMapping("/add")
    public ServiceResult add(@RequestBody OperationLogRequest request, HttpServletRequest httpServletRequest) {
        request.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        operateLogService.create(request);
        return ResultCommon.success();
    }
    @ApiOperation(value = "新增操作日志")
    @ApiImplicitParam(name = "request", value = "操作日志请求", dataTypeClass = OperationLogRequest.class)
    @PostMapping("/adds")
    @OperateLog( module = OpModuleEnums.tool,type = OpTypeEnums.DOWNLOAD, title = "下载",target = OpTargetEnums.TARGET)
    public ServiceResult add(@RequestBody JSONObject json, HttpServletRequest httpServletRequest) {
        val request = JSONObject.parseObject(json.toString(), OperationLogRequest.class);
        request.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        operateLogService.createToolLog(request);
        return ResultCommon.success();
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/page")
    public ListObjServiceResult<OperationLogPageResponse> page(OperationLogPageRequest request) {
        request.ensure();
        return operateLogService.search(request);
    }

    @ApiOperation("批量删除日志")
    @ApiImplicitParam(name = "ids", value = "删除数据请求体", dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return operateLogService.bulkDeleteByIds(ids).asResult();
    }

    @GetMapping("/modules")
    @ApiOperation(value = "查询审计日志操作模块")
    public ServiceResult<List<OptionBO>> getModules() {
        return optionService.getIntOptions(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_MODULE);
    }

    /**
     *
     * @param type module audio-log 审计日志模块  operate-log 操作日志模块
     * @return
     */
    @GetMapping("/moduless")
    @ApiOperation(value = "查询审计日志操作模块")
    public ServiceResult<List<OptionBO>> getModules(String type) {
        val intOptions = optionService.getIntOptions(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_MODULE);

        if (StringUtils.equals(LogConstant.AUDIO_LOG, type)) { // 审计日志模块
            List<Integer> list1 = LogConstant.AUDIO_LOG_CODES;

            val boList = intOptions.getSource().stream().filter(s ->list1.contains(s.getCode())).collect(Collectors.toList());
            intOptions.setData(boList);

        }
        if (StringUtils.equals(LogConstant.OPERATE_LOG, type)) { // 操作日志模块
            List<Integer> list1 = LogConstant.OPERATE_LOG_CODES;

            val boList = intOptions.getSource().stream().filter(s ->list1.contains(s.getCode())).collect(Collectors.toList());
            intOptions.setData(boList);
        }

        return intOptions;
    }

    @GetMapping("/types")
    @ApiOperation(value = "查询审计日志操作类型")
    public ServiceResult<List<OptionBO>> getTypes() {
        return optionService.getIntOptions(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_TYPE);
    }

    @ApiOperation(value = "导出日志excel", notes = "")
    @ApiImplicitParam(name = "exportRequest", value = "导出请求", dataTypeClass = LogsExportrequest.class)
    @PostMapping("/excel/export")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void exportLogExcel(HttpServletResponse response, @RequestBody LogsExportrequest exportRequest) throws IOException {
//        operateLogService.exportExcel(response, exportRequest);
        exportHandle.exportExcel(response, exportRequest);
    }

}
