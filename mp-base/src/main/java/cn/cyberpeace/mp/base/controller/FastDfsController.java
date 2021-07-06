package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @Description: FastDFS控制层
 *               trackerServers每个接口初始化一次，便于不同业务对接不同集群的FastDFS
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@RestController
@Slf4j
public class FastDfsController {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private FastDfsConfig fastDfsConfig;

    /**
     * 上传文件通用，只上传文件到服务器
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @PostMapping(value = "/file/upload")
    @ResponseBody
    public ServiceResult uploadFile(MultipartFile file, HttpServletRequest request) {
        FileResponseData responseData = uploadSample(file, request);
        return ServiceResult.success(responseData);
    }

    /**
     * 以附件形式下载文件
     *
     * @param filePath 文件地址
     * @param response
     */
    @PostMapping("/file/download")
    public void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException {
        try {
            fastDFSClient.downloadFile(filePath, response, FdfsGroupEnum.DEFAULT.toString());
        } catch (MpRuntimeException e) {
            log.error("downloadFile error", e);
            throw e;
        }
    }


    /**
     * 根据指定的路径删除服务器文件
     *
     * @param filePath
     */
    @DeleteMapping("/file/delete")
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
     * @param request
     * @return
     */
    public FileResponseData uploadSample(MultipartFile file, HttpServletRequest request) {
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
        } catch (MpRuntimeException e) {
            responseData.setSuccess(false);
            responseData.setCode(e.getErrorCode());
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }

}
