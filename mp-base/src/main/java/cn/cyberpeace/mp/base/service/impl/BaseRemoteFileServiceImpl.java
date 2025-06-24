 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseRemoteFileServiceImpl.java.
 // File size 6727.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName BaseRemoteFileServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRemoteFileService
 */
@Slf4j
@Data
public abstract class BaseRemoteFileServiceImpl implements BaseRemoteFileService {

    /**
     * upload
     *
     * @param localFilepath
     * @return
     */
    @Override
    public ServiceResult<String> upload(String localFilepath) {
        ServiceResult<Boolean> validateResult = validateUploadLocalFile(localFilepath);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        String remoteFileKey;
        try {
            remoteFileKey = FsCommon.upload(localFilepath);
        } catch (Exception e) {
            log.error("Service upload file {} error: {}", localFilepath, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(remoteFileKey);
    }

    /**
     * uploadContent
     *
     * @param content
     * @param fileSuffix
     * @return
     */
    @Override
    public ServiceResult<String> uploadContent(String content, String fileSuffix) {
        ServiceResult<Boolean> validateResult = validateUploadContent(content, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        String remoteFileKey;
        try {
            remoteFileKey = FsCommon.uploadContent(content, fileSuffix);
        } catch (Exception e) {
            log.error("Service upload file content with suffix {} error: {}", fileSuffix, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(remoteFileKey);
    }

    /**
     * upload
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    @Override
    public ServiceResult<String> upload(InputStream is, String fileSuffix) {
        ServiceResult<Boolean> validateResult = validateUploadInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        String remoteFileKey;
        try {
            remoteFileKey = FsCommon.upload(is, fileSuffix);
        } catch (Exception e) {
            log.error("Service upload file input stream with suffix {} error: {}", fileSuffix, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(remoteFileKey);
    }


}
