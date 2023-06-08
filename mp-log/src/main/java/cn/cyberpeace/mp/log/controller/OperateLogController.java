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
import cn.hutool.core.exceptions.ExceptionUtil;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        request.ensure();
        var result = operateLogService.search(request).getSource().getObjs();
        String header = "用户名,模块,操作类型,操作对象,操作时间,内容";
        File file = new File("audit_logs.csv");
        InputStream fis = null;
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                String filename = file.getName();
                file.createNewFile();
                StringBuilder stringBuilder = new StringBuilder();
                for (var logResponse : result) {
                    stringBuilder.append(StringUtils.joinWith(",",
                            logResponse.getUserName(), logResponse.getOpModule(), logResponse.getOpType(),
                            logResponse.getOpTarget(), logResponse.getOpTime(), logResponse.getContent().replace(",", "")))
                            .append('\r').append('\n');
                }
                String data = stringBuilder.toString();
                this.writeStringToCsv(header, data, file.getPath());
                fileInputStream = new FileInputStream(file);
                fis = new BufferedInputStream(fileInputStream);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);

                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.addHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
                response.addHeader("Content-Length", "" + file.length());
                outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                outputStream.write(buffer);
                outputStream.flush();
            } catch (Exception e) {
                log.error("create file error: {}", ExceptionUtil.stacktraceToOneLineString(e));
            } finally {
                try {
                    if (Objects.nonNull(fileInputStream)) {
                        fileInputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    file.delete();
                } catch (Exception ignored) {

                }
            }
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
