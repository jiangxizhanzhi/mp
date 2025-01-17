 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileService.java.
 // File size 626.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件操作接口
 */
public interface FileService {

    ServiceResult<FileResponseData> uploadFile(MultipartFile file);

    void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException;

    ServiceResult deleteFile(String filePath);


}
