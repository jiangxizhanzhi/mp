 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MarkdownFileService.java.
 // File size 2717.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @ClassName MarkdownFileService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRemoteFileServiceImpl
 */
@Service
public class MarkdownFileService extends BaseRemoteFileServiceImpl {

    /**
     * validateUploadImageInput
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    public ServiceResult<Boolean> validateUploadImageInput(InputStream is, String fileSuffix) {
        // MD上传图片支持类型校验
        if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.MD_UPLOAD_IMAGE_LIST)) {
            return new ServiceResult<>(false, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return new ServiceResult<>(true);
    }

    /**
     * validateUploadFileInput
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    public ServiceResult<Boolean> validateUploadFileInput(InputStream is, String fileSuffix) {
        // MD上传文件支持类型校验
        if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.MD_UPLOAD_FILE_LIST)) {
            return new ServiceResult<>(false, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return new ServiceResult<>(true);
    }

    /**
     * uploadImage
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    public ServiceResult<String> uploadImage(InputStream is, String fileSuffix) {
        ServiceResult<Boolean> validateResult = validateUploadImageInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

    /**
     * uploadFile
     *
     * @param is
     * @param fileSuffix
     * @return
     */
    public ServiceResult<String> uploadFile(InputStream is, String fileSuffix) {
        ServiceResult<Boolean> validateResult = validateUploadFileInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

}
