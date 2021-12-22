package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/22 13:57
 */
@Slf4j
public class ExcelCheck {

    // 最大100MB
    public static final long MAX_SIZE = 100 * 1024 * 1024;

    public static final String[] EXCEL_TYPES = {"xls", "xlsx"};

    public static void check(MultipartFile file) {
        ParamCheck.notNull(file, AuthBasei18nCode.REQUEST_PARAMS);

        String type = "";
        try {
            int begin = file.getOriginalFilename().indexOf(".");
            int last = file.getOriginalFilename().length();
            type = file.getOriginalFilename().substring(begin + 1, last);
//            type = FileTypeUtil.getType(file.getInputStream());
        } catch (Exception e) {
            log.error("not an excel file, ex:{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(AuthErrorCode.EXCEL_FORMAT_ERROR);
        }

        boolean typeMatch = false;
        if (StringUtils.isNotBlank(type)) {
            for (String excelType : EXCEL_TYPES) {
                if (type.equals(excelType)) {
                    typeMatch = true;
                    break;
                }
            }
        }

        if (!typeMatch) {
            log.error("not an excel file, type:{}", type);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_NOT_EXCEL);
        }


        long size = file.getSize();
        log.info("size:{}", size);
        if (size > MAX_SIZE) {
            log.error("over size, size:{}", size);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_EXCEED_SIZE);
        }

    }

}
