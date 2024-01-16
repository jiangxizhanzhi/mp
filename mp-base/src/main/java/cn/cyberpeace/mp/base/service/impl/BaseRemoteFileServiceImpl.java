package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Data
public abstract class BaseRemoteFileServiceImpl implements BaseRemoteFileService {

    @Override
    public ServiceResult<String> upload(String localFilepath) {
        var validateResult = validateUploadLocalFile(localFilepath);
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

    @Override
    public ServiceResult<String> uploadContent(String content, String fileSuffix) {
        var validateResult = validateUploadContent(content, fileSuffix);
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

    @Override
    public ServiceResult<String> upload(InputStream is, String fileSuffix) {
        var validateResult = validateUploadInput(is, fileSuffix);
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

    @Override
    public void download(String remoteFileKey, OutputStream os) {
        FsCommon.download(remoteFileKey, os);
    }

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

    @Override
    public ServiceResult<String> upload(InputStream is, long fileSize, String fileSuffix) {
        var validateResult = validateUploadInput(is, fileSuffix);
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
