 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AvatarService.java.
 // File size 1559.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.common.FileCheckCommon;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.util.FileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName AvatarService
 * @Description 头像管理service
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Service
@Slf4j
public class AvatarService {

    /**
     * upload
     *
     * @param file
     * @return
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
        return FileClient.uploadSample(file);
    }
}
