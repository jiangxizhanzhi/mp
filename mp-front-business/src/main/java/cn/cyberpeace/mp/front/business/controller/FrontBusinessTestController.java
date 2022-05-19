package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.util.ExcelCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.business.dto.ImportUrlAuth;
import cn.cyberpeace.mp.front.business.service.UploadUrlAuthListener;
import cn.cyberpeace.mp.front.business.service.UrlACService;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelCommonException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.exception.ExcelGenerateException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 17:59
 */
@Slf4j
@Controller
public class FrontBusinessTestController {

    @Autowired
    private UrlACService urlACService;

    @ApiOperation(value = "导入AuthUrl", notes = "")
    @PostMapping("/excel/importAuthUrl")
    @ResponseBody
    public ServiceResult importAuthUrls(MultipartFile file) throws IOException {
        ExcelCheck.check(file);
        try {
            EasyExcel.read(file.getInputStream(), ImportUrlAuth.class, new UploadUrlAuthListener(urlACService)).headRowNumber(1).sheet().doRead();
        } catch (ExcelCommonException | ExcelAnalysisException | ExcelDataConvertException | ExcelGenerateException ex) {
            log.error("ex:{}", ExceptionUtil.stacktraceToString(ex));
            throw new MpRuntimeException(AuthErrorCode.EXCEL_FORMAT_ERROR);
        }
        return ServiceResult.successful();
    }

}
