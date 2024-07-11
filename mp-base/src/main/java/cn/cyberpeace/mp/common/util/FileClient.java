package cn.cyberpeace.mp.common.util;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName FileClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class FileClient {


    /**
     * 文件上传
     * @param file
     * @return
     */
    public static ServiceResult<FileResponseData> uploadFile(MultipartFile file) {
        FileResponseData responseData = uploadSample(file);
        if (responseData.isSuccess()) {
            return ResultCommon.success(responseData);
        } else {
            return ResultCommon.fail(responseData.getMessage());
        }

    }

    /**
     * 文件下载
     * @param filePath
     * @param response
     * @throws MpRuntimeException
     */
    public static void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException {
        try {
            byte[] fileByte = FsCommon.downloadRawContent(filePath);
            if (ArrayUtils.isEmpty(fileByte)) {
                log.error("downloadFile not exist");
                throw new MpRuntimeException(CommonErrorCode.FILE_DOWNLOAD_FAILED);
            }

            try (InputStream is = new ByteArrayInputStream(fileByte);
                 OutputStream os = response.getOutputStream();) {
                byte[] buffer = new byte[1024 * 5];
                int len = 0;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("downloadFile error", e);
            throw new MpRuntimeException(CommonErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }


    /**
     * 上传通用方法，只上传到服务器，不保存记录到数据库
     *
     * @param file
     * @return
     */
    public static FileResponseData uploadSample(MultipartFile file) {
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
