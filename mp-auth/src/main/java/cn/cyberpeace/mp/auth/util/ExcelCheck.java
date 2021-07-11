package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


@Slf4j
public class ExcelCheck {

    
    public static final long MAX_SIZE = 100 * 1024 * 1024;

    public static final String[] EXCEL_TYPES = {"xls", "xlsx"};

    public static void check(InputStream inputStream, String fileName) {
        String type = null;
        if (StringUtils.isNotEmpty(fileName)){
            type = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        boolean typeMatch = false;
        if (StringUtils.isNotBlank(type)) {
            for (String excelType : EXCEL_TYPES) {
                if (Objects.equals(type, excelType)) {
                    typeMatch = true;
                    break;
                }
            }
        }

        if (!typeMatch) {
            log.error("not an excel file, type:{}", type);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_NOT_EXCEL);
        }

        long size = 0;
        try {
            size = inputStream.available();
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
        if (size > MAX_SIZE) {
            log.error("over size, size:{}", size);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_EXCEED_SIZE);
        }

    }
}
