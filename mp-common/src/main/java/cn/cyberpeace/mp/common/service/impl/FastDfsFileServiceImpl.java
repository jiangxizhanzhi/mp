package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.common.FdfsCommon;
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

    /**
     * 文件下载
     * @param filePath
     * @param response
     * @throws MpRuntimeException
     */
    @Override
    public void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] fileByte = FdfsCommon.downloadRawContent(filePath);
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

    /**
     * 文件删除
     * @param filePath
     * @return
     */
    @Override
    public ServiceResult<Void> deleteFile(String filePath) {
        try {
            FdfsCommon.delete(filePath);
        } catch (MpRuntimeException e) {
            log.error("deleteFile error", e);
            return ResultCommon.fail(e.getMessage());
        }
        return ResultCommon.success();
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
            String filepath = FdfsCommon.upload(file.getInputStream(), suffix);
            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(suffix);
            responseData.setHttpUrl(FdfsCommon.genHttpUrl(filepath));
            responseData.setInnerHttpUrl(FdfsCommon.genInnerHttpUrl(filepath));
            responseData.setFileSize(file.getSize());
        } catch (MpRuntimeException | IOException e) {
            responseData.setSuccess(false);
            responseData.setMessage(MessageCommon.get(e.getMessage()));
        }

        return responseData;
    }

}
