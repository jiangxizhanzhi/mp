 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MarkdownFileService.java.
 // File size 2309.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MarkdownFileService extends BaseRemoteFileServiceImpl {

    @Autowired
    public MarkdownFileService(FsCommon fsCommon) {
        setFileClient(FsCommon.fileClient);
    }

    public ServiceResult<Boolean> validateUploadImageInput(InputStream is, String fileSuffix) {
        // MD上传图片支持类型校验
        if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.MD_UPLOAD_IMAGE_LIST)) {
            return new ServiceResult<>(false, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> validateUploadFileInput(InputStream is, String fileSuffix) {
        // MD上传文件支持类型校验
        if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.MD_UPLOAD_FILE_LIST)) {
            return new ServiceResult<>(false, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<String> uploadImage(InputStream is, String fileSuffix) {
        var validateResult = validateUploadImageInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

    public ServiceResult<String> uploadFile(InputStream is, String fileSuffix) {
        var validateResult = validateUploadFileInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

}
