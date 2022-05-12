package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.FileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @Description: FastDFS文件操作实现类
 *               trackerServers每个接口初始化一次，便于不同业务对接不同集群的FastDFS
 * @Author qiaokang
 * @Date：2021/7/29 14:47
 */
@Slf4j
@Service
public class FastDfsFileServiceImpl implements FileService {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FastDfsConfig fastDfsConfig;

    /**
     * 文件上传
     * @param file
     * @param request
     * @return
     */
    @Override
    public ServiceResult<FileResponseData> uploadFile(MultipartFile file, HttpServletRequest request) {
        FileResponseData responseData = uploadSample(file);
        if (responseData.isSuccess()) {
            return ServiceResult.success(responseData);
        } else {
            return ServiceResult.fail(responseData.getMessage());
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
        try {
            fastDFSClient.downloadFile(filePath, response, FdfsGroupEnum.DEFAULT.toString());
        } catch (MpRuntimeException e) {
            log.error("downloadFile error", e);
            throw e;
        }
    }

    /**
     * 文件删除
     * @param filePath
     * @return
     */
    @Override
    public ServiceResult deleteFile(String filePath) {
        try {
            fastDFSClient.deleteFile(filePath, FdfsGroupEnum.DEFAULT.toString());
        } catch (MpRuntimeException e) {
            log.error("deleteFile error", e);
            return ServiceResult.fail(e.getMessage());
        }
        return ServiceResult.successful();
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
            String filepath = fastDFSClient.uploadFileWithMultipart(file, FdfsGroupEnum.DEFAULT.toString());

            // 获取当前集群配置
            FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);

            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(FastDFSClient.getFilenameSuffix(file.getOriginalFilename()));
            responseData.setHttpUrl(group.getFileServerAddr() + File.separator + filepath);
            responseData.setInnerHttpUrl(group.getInnerFileServerAddr() + File.separator + filepath);
            responseData.setFileSize(file.getSize());
        } catch (MpRuntimeException e) {
            responseData.setSuccess(false);
//            responseData.setCode(e.getErrorCode());
            responseData.setMessage(MessageCommon.get(e.getMessage()));
        }

        return responseData;
    }

}
