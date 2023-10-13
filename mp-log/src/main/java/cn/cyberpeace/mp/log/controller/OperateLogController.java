package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.dto.OperateExportDTO;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户操作日志记录
 * @author wanggang
 */
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

    @ApiOperation(value = "下载操作日志")
    @GetMapping("/download")
    public void download(OperationLogPageRequest request, HttpServletResponse response) {
        request.setPage(null);
        request.setPerPage(null);
        if (Objects.isNull(request.getEndDate())) {
            request.setEndDate(LocalDateTime.now());
        }
        if (Objects.isNull(request.getStartDate())) {
            request.setStartDate(request.getEndDate().minusDays(100L));
        }
        var result = operateLogService.search(request).getSource().getObjs();
        List<OperateExportDTO> dtos = new ArrayList<>();
        for (var logResponse : result) {
            OperateExportDTO operateExportDTO = new OperateExportDTO();
            operateExportDTO.setName(logResponse.getUserName());
            operateExportDTO.setModule(logResponse.getOpModule());
            operateExportDTO.setOperateType(logResponse.getOpType());
            operateExportDTO.setOperateTarget(logResponse.getOpTarget());
            operateExportDTO.setOperateTime(logResponse.getOpTime().toString());
            operateExportDTO.setContent(logResponse.getContent().replace(",", ""));
            dtos.add(operateExportDTO);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            String fileName = URLEncoder.encode("审计日志", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), OperateExportDTO.class).sheet("审计日志").doWrite(dtos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private void writeStringToCsv(String head, String dataStr, String csvFilePath) {
        // 将流写在try里面，当try执行完之后，流会自动关闭
        try (FileOutputStream fileOutputStream = new FileOutputStream(csvFilePath, false);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            if (StringUtils.isNotBlank(head)) {
                bufferedWriter.write(head);
                bufferedWriter.newLine();
            }

            // 将数据写入到文件中
            if (StringUtils.isNotBlank(dataStr)) {
                bufferedWriter.write(dataStr);
            }

            //把缓存中的数据输出到CSV文件
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
