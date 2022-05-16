package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.io.InputStream;
import java.io.OutputStream;

public interface BaseRemoteFileService {

    default ServiceResult<Boolean> validateUploadLocalFile(String localFilepath) {
        return new ServiceResult<>(true);
    }

    default ServiceResult<Boolean> validateUploadContent(String content, String fileSuffix) {
        return new ServiceResult<>(true);
    }

    default ServiceResult<Boolean> validateUploadInput(InputStream is, String fileSuffix) {
        return new ServiceResult<>(true);
    }

    ServiceResult<String> upload(String localFilepath);

    default ServiceResult<String> uploadContent(String content) {
        return uploadContent(content, null);
    }

    ServiceResult<String> uploadContent(String content, String fileSuffix);

    default ServiceResult<String> upload(InputStream is) {
        return upload(is, null);
    }

    ServiceResult<String> upload(InputStream is, String fileSuffix);

    void download(String remoteFileKey, OutputStream os);

    ServiceResult<Boolean> download(String remoteFileKey, String localFilepath);

    ServiceResult<String> downloadTmp(String remoteFileKey);

    ServiceResult<String> downloadContent(String remoteFileKey);

    ServiceResult<Boolean> delete(String remoteFileKey);

}
