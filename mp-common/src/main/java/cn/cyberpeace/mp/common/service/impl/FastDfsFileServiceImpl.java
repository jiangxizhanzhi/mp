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


@Slf4j
@Service
public class FastDfsFileServiceImpl implements FileService {

    
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
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] fileByte = FsCommon.downloadRawContent(filePath);
            if (ArrayUtils.isEmpty(fileByte)) {
                log.error("downloadFile not exist");
                throw new MpRuntimeException(CommonErrorCode.FILE_DOWNLOAD_FAILED);
            }
            is = new ByteArrayInputStream(fileByte);
            os = response.getOutputStream();
            byte[] buffer = new byte[1024 * 5];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("downloadFile error", e);
            throw new MpRuntimeException(CommonErrorCode.FILE_DOWNLOAD_FAILED);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    
    @Override
    public ServiceResult<Void> deleteFile(String filePath) {
        try {
            FsCommon.delete(filePath);
        } catch (MpRuntimeException e) {
            log.error("deleteFile error", e);
            return ResultCommon.fail(e.getMessage());
        }
        return ResultCommon.success();
    }

    
    private FileResponseData uploadSample(MultipartFile file) {
        FileResponseData responseData = new FileResponseData();
        try {
            
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
