package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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

    public static void check(File file) {
        ParamCheck.notNull(file, AuthBasei18nCode.REQUEST_PARAMS);
        String fileName = file.getName();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);

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

        long size = file.length();
        if (size > MAX_SIZE) {
            log.error("over size, size:{}", size);
            throw new MpRuntimeException(AuthErrorCode.EXCEL_EXCEED_SIZE);
        }

    }

    /**
     * 将 MultipartFile 类型转成 File
     *
     * @param multipartFile
     * @return
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        ParamCheck.notNull(multipartFile, AuthBasei18nCode.REQUEST_PARAMS);
        String filename = multipartFile.getOriginalFilename();
        // multipartFile.getOriginalFilename() is nullable
        // pathname of new File() is notnull
        if (Objects.isNull(filename)) {
            log.error("fileName is null");
            throw new MpRuntimeException(AuthErrorCode.FILE_NAME_ISNULL_ERROR);
        }
        File file = new File(filename);
        try {
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
        } catch (IOException e) {
            log.error("copy file failed:{}", e.getMessage());
            throw new MpRuntimeException(AuthErrorCode.COPY_FILE_ERROR);
        }
        return file;
    }



}
