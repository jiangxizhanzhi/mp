 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AvatarService.java.
 // File size 2233.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.common.FileCheckCommon;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

 /**
  * 《阿凡达》服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class AvatarService {

     /**
      * 上传
      *
      * @param file 文件
      * @return {@link FileResponseData }
      */
     public FileResponseData upload(MultipartFile file) {
        // 校验文件
        FileCheckRequest fileCheckRequest = new FileCheckRequest();
        fileCheckRequest.setMaxSize(AuthConstant.UPLOAD_AVATAR_MAX_SIZE);
        fileCheckRequest.setSuffixList(AuthConstant.UPLOAD_AVATAR_SUFFIX_TYPE);
        fileCheckRequest.setMagicHeaderList(AuthConstant.UPLOAD_AVATAR_MAGIC_HEADER_TYPE);
        ServiceResult<Boolean> uploadResult = FileCheckCommon.checkFile(file,fileCheckRequest);
        if (!Boolean.TRUE.equals(uploadResult.getSource())) {
            throw new MpRuntimeException(uploadResult.getServiceCode());
        }
        FileResponseData responseData = new FileResponseData();
        try {
            // 上传到服务器
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            String filepath = FsCommon.upload(file.getInputStream(), suffix);
            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(suffix);
            responseData.setHttpUrl(FsCommon.genHttpUrl(filepath));
            responseData.setInnerHttpUrl(FsCommon.genInnerHttpUrl(filepath));
        } catch (MpRuntimeException | IOException e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }
}
