 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileCheckCommon.java.
 // File size 3736.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
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

 /**
  * 文件检查
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class FileCheckCommon {

     /**
      * 检查文件
      *
      * @param file             文件
      * @param fileCheckRequest 文件检查请求
      * @return {@link ServiceResult }<{@link Boolean }>
      */
     public static ServiceResult<Boolean> checkFile(MultipartFile file, FileCheckRequest fileCheckRequest) {
        boolean match = true;
        if (Objects.nonNull(fileCheckRequest.getMaxSize())) {
            // 校验图片大小
            Long maxSize = fileCheckRequest.getMaxSize();
            match = checkSize(file, maxSize);
            if (!match) {
                maxSize = maxSize / 1024 / 1024;
                return new ServiceResult<>(false, AuthErrorCode.FILE_OVERSIZE_ERROR.format(String.valueOf(maxSize)));
            }
        }

        if (CollUtil.isNotEmpty(fileCheckRequest.getSuffixList())) {
            // 校验文件后缀类型
            List<String> suffixList = fileCheckRequest.getSuffixList();
            match = checkSuffix(file, suffixList);
            if (!match) {
                String types = String.join(AuthConstant.JOIN_TYPES_DELIMITER, suffixList);
                return new ServiceResult<>(false, AuthErrorCode.FILE_SUFFIX_TYPE_ERROR.format(types));
            }
        }

        if (CollUtil.isNotEmpty(fileCheckRequest.getMagicHeaderList())) {
            // 根据文件头判断文件类型
            List<String> magicHeaderList = fileCheckRequest.getMagicHeaderList();
            match = checkMagicHeader(file, magicHeaderList);
            if (!match) {
                String headers = String.join(AuthConstant.JOIN_TYPES_DELIMITER, magicHeaderList);
                return new ServiceResult<>(false, AuthErrorCode.FILE_MAGIC_HEADER_ERROR.format(headers));
            }
        }

        return new ServiceResult<>(true);
    }

     /**
      * 检查尺寸
      *
      * @param file    文件
      * @param maxSize
      * @return boolean
      */
     public static boolean checkSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

     /**
      * @param fileName
      * @param suffixList
      * @return boolean
      */
     public static boolean checkSuffix(String fileName, List<String> suffixList) {
        String suffix = FileUtil.getSuffix(fileName);
        return suffixList.stream().anyMatch(x -> x.equalsIgnoreCase(suffix));
    }

     /**
      * @param file       文件
      * @param suffixList
      * @return boolean
      */
     public static boolean checkSuffix(MultipartFile file, List<String> suffixList) {
        return checkSuffix(file.getOriginalFilename(), suffixList);
    }

     /**
      * @param inputStream
      * @param magicHeaderList
      * @return boolean
      */
     public static boolean checkMagicHeader(InputStream inputStream, List<String> magicHeaderList) {
        String type = FileTypeUtil.getType(inputStream);
        if (StringUtils.isBlank(type)) {
            // 无法获取文件头信息，直接返回true
            return true;
        }
        return magicHeaderList.stream().anyMatch(x -> x.equalsIgnoreCase(type));
    }

     /**
      * @param file            文件
      * @param magicHeaderList
      * @return boolean
      */
     public static boolean checkMagicHeader(MultipartFile file, List<String> magicHeaderList) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
        return checkMagicHeader(inputStream, magicHeaderList);
    }

     /**
      * 文件检查
      */
     private FileCheckCommon() {
    }
}
