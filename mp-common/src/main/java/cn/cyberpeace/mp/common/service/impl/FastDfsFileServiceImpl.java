 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FastDfsFileServiceImpl.java.
 // File size 4360.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.service.FileService;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description: FastDFS文件操作实现类
 *               trackerServers每个接口初始化一次，便于不同业务对接不同集群的FastDFS
 * @Author qiaokang
 * @Date：2021/7/29 14:47
 */
@Slf4j
@Service
public class FastDfsFileServiceImpl implements FileService {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @Override
    public ServiceResult<FileResponseData> uploadFile(MultipartFile file) {
        FileResponseData responseData = uploadSample(file);
        if (responseData.isSuccess()) {
            return ResultCommon.success(responseData);
        } else {
            return ResultCommon.fail(responseData.getMessage());
        }

    }

    @Override
    public void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException {

    }

    @Override
    public ServiceResult deleteFile(String filePath) {
        return null;
    }


    /**
     * 上传通用方法，只上传到服务器，不保存记录到数据库
     *
     * @param file
     * @return
     */
    private FileResponseData uploadSample(MultipartFile file) {
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
            responseData.setFileSize(file.getSize());
        } catch (MpRuntimeException | IOException e) {
            responseData.setSuccess(false);
            responseData.setMessage(MessageCommon.get(e.getMessage()));
        }

        return responseData;
    }

}
