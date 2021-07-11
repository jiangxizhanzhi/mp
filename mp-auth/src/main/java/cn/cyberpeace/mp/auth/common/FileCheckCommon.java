package cn.cyberpeace.mp.auth.common;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


public class FileCheckCommon {

    public static ServiceResult<Boolean> checkFile(MultipartFile file, FileCheckRequest fileCheckRequest) {
        boolean match = true;
        if (Objects.nonNull(fileCheckRequest.getMaxSize())) {
            
            Long maxSize = fileCheckRequest.getMaxSize();
            match = checkSize(file, maxSize);
            if (!match) {
                maxSize = maxSize / 1024 / 1024;
                return new ServiceResult<>(false, AuthErrorCode.FILE_OVERSIZE_ERROR.format(String.valueOf(maxSize)));
            }
        }

        if (CollUtil.isNotEmpty(fileCheckRequest.getSuffixList())) {
            
            List<String> suffixList = fileCheckRequest.getSuffixList();
            match = checkSuffix(file, suffixList);
            if (!match) {
                String types = String.join(AuthConstant.JOIN_TYPES_DELIMITER, suffixList);
                return new ServiceResult<>(false, AuthErrorCode.FILE_SUFFIX_TYPE_ERROR.format(types));
            }
        }

        if (CollUtil.isNotEmpty(fileCheckRequest.getMagicHeaderList())) {
            
            List<String> magicHeaderList = fileCheckRequest.getMagicHeaderList();
            match = checkMagicHeader(file, magicHeaderList);
            if (!match) {
                String headers = String.join(AuthConstant.JOIN_TYPES_DELIMITER, magicHeaderList);
                return new ServiceResult<>(false, AuthErrorCode.FILE_MAGIC_HEADER_ERROR.format(headers));
            }
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
        if (StringUtils.isBlank(type)) {
            
            return true;
        }
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
