package cn.cyberpeace.mp.auth.common;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/10/26
 */
public class FileCheckCommon {

    public static ServiceResult<Boolean> checkFile(MultipartFile file, long maxSize, List<String> suffixList, List<String> magicHeaderList) {
        // 校验图片大小
        boolean match = checkSize(file, maxSize);
        if (!match) {
            maxSize = maxSize / 1024 / 1024;
            return new ServiceResult<>(false, AuthErrorCode.FILE_OVERSIZE_ERROR.format(String.valueOf(maxSize)));
        }

        // 校验文件后缀类型
        match = checkSuffix(file, suffixList);
        if (!match) {
            String types = String.join(", ", suffixList);
            return new ServiceResult<>(false, AuthErrorCode.FILE_SUFFIX_TYPE_ERROR.format(types));
        }

        // 根据文件头判断文件类型
        match = checkMagicHeader(file, magicHeaderList);
        if (!match) {
            String headers = String.join(", ", magicHeaderList);
            return new ServiceResult<>(false, AuthErrorCode.FILE_MAGIC_HEADER_ERROR.format(headers));
        }

        return new ServiceResult<>(true);
    }

    public static boolean checkSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

    public static boolean checkSuffix(String fileName, List<String> suffixList) {
        String suffix = FileUtil.getSuffix(fileName);
        return suffixList.stream().anyMatch(x -> x.equalsIgnoreCase(suffix));
    }

    public static boolean checkSuffix(MultipartFile file, List<String> suffixList) {
        return checkSuffix(file.getOriginalFilename(), suffixList);
    }

    public static boolean checkMagicHeader(InputStream inputStream, List<String> magicHeaderList) {
        String type = FileTypeUtil.getType(inputStream);
        return magicHeaderList.stream().anyMatch(x -> x.equalsIgnoreCase(type));
    }

    public static boolean checkMagicHeader(MultipartFile file, List<String> magicHeaderList) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
        return checkMagicHeader(inputStream, magicHeaderList);
    }

    private FileCheckCommon() {
    }
}
