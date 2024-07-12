 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseRemoteFileService.java.
 // File size 3026.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName BaseRemoteFileService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface BaseRemoteFileService {

    /**
     * validateUploadLocalFile
     *
     * @param localFilepath
     * @return
     */
    default ServiceResult<Boolean> validateUploadLocalFile(String localFilepath) {
        return new ServiceResult<>(true);
    }

    /**
     * validateUploadContent
     *
     * @param content
     * @param fileSuffix
     * @return
     */
    default ServiceResult<Boolean> validateUploadContent(String content, String fileSuffix) {
        return new ServiceResult<>(true);
    }

    /**
     * validateUploadInput
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    default ServiceResult<Boolean> validateUploadInput(InputStream is, String fileSuffix) {
        return new ServiceResult<>(true);
    }

    /**
     * upload
     *
     * @param localFilepath
     * @return
     */
    ServiceResult<String> upload(String localFilepath);

    /**
     * uploadContent
     *
     * @param content
     * @return
     */
    default ServiceResult<String> uploadContent(String content) {
        return uploadContent(content, null);
    }

    /**
     * uploadContent
     *
     * @param content
     * @param fileSuffix
     * @return
     */
    ServiceResult<String> uploadContent(String content, String fileSuffix);

    /**
     * upload
     *
     * @param is
     * @return
     */
    default ServiceResult<String> upload(InputStream is) {
        return upload(is, null);
    }

    /**
     * upload
     *
     * @param is
     * @param fileSize
     * @param fileSuffix
     * @return
     */
    ServiceResult<String> upload(InputStream is, long fileSize, String fileSuffix);

    /**
     * upload
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    ServiceResult<String> upload(InputStream is, String fileSuffix);

    /**
     * download
     *
     * @param remoteFileKey
     * @param os
     */
    void download(String remoteFileKey, OutputStream os);

    /**
     * download
     *
     * @param remoteFileKey
     * @param localFilepath
     * @return
     */
    ServiceResult<Boolean> download(String remoteFileKey, String localFilepath);

    /**
     * downloadTmp
     *
     * @param remoteFileKey
     * @return
     */
    ServiceResult<String> downloadTmp(String remoteFileKey);

    /**
     * downloadContent
     *
     * @param remoteFileKey
     * @return
     */
    ServiceResult<String> downloadContent(String remoteFileKey);

    /**
     * delete
     *
     * @param remoteFileKey
     * @return
     */
    ServiceResult<Boolean> delete(String remoteFileKey);

}
