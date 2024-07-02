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

    /**
     * download
     *
     * @param remoteFileKey
     * @param os
     */
    @Override
    public void download(String remoteFileKey, OutputStream os) {
        FsCommon.download(remoteFileKey, os);
    }

    /**
     * download
     *
     * @param remoteFileKey
     * @param localFilepath
     * @return
     */
    @Override
    public ServiceResult<Boolean> download(String remoteFileKey, String localFilepath) {
        boolean result;
        try {
            result = FsCommon.download(remoteFileKey, localFilepath);
        } catch (Exception e) {
            log.error("Service download file {} to {} error: {}", remoteFileKey, localFilepath, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(result);
    }

    /**
     * downloadTmp
     *
     * @param remoteFileKey
     * @return
     */
    @Override
    public ServiceResult<String> downloadTmp(String remoteFileKey) {
        String localFilepath;
        try {
            localFilepath = FsCommon.downloadTmp(remoteFileKey);
        } catch (Exception e) {
            log.error("Service download file {} for tmp error: {}", remoteFileKey, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(localFilepath);
    }

    /**
     * downloadContent
     *
     * @param remoteFileKey
     * @return
     */
    @Override
    public ServiceResult<String> downloadContent(String remoteFileKey) {
        String content;
        try {
            content = FsCommon.downloadContent(remoteFileKey);
        } catch (Exception e) {
            log.error("Service download file {} content error: {}", remoteFileKey, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(content);
    }

    /**
     * delete
     *
     * @param remoteFileKey
     * @return
     */
    @Override
    public ServiceResult<Boolean> delete(String remoteFileKey) {
        boolean result;
        try {
            result = FsCommon.delete(remoteFileKey);
        } catch (Exception e) {
            log.error("Service delete file {} error: {}", remoteFileKey, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(result);
    }

    /**
     * upload
     *
     * @param is
     * @param fileSize
     * @param fileSuffix
     * @return
     */
    @Override
    public ServiceResult<String> upload(InputStream is, long fileSize, String fileSuffix) {
        ServiceResult<Boolean> validateResult = validateUploadInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        String remoteFileKey;
        try {
            remoteFileKey = FsCommon.upload(is, fileSize, fileSuffix);
        } catch (Exception e) {
            log.error("Service upload file input stream with suffix {} error: {}", fileSuffix, e.getMessage());
            return new ServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        return new ServiceResult<>(remoteFileKey);
    }
}
