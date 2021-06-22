package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.base.fdfsclient.FastDFSException;
import cn.cyberpeace.mp.base.fdfsclient.FileResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: FastDFS控制层
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@RestController
@Slf4j
public class FastDfsController {

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 文件服务器地址
     */
    @Value("${file_server_addr}")
    private String fileServerAddr;

    /**
     * 上传文件通用，只上传文件到服务器
     *
     * @param file
     * @param request
     * @return 返回文件路径等信息
     */
    @PostMapping(value = "/file/upload")
    @ResponseBody
    public FileResponseData uploadFile(MultipartFile file, HttpServletRequest request) {
        return uploadSample(file, request);
    }

    /**
     * 以附件形式下载文件
     *
     * @param filePath 文件地址
     * @param response
     */
    @PostMapping("/file/download")
    public void downloadFile(String filePath, HttpServletResponse response) throws FastDFSException {
        try {
            fastDFSClient.downloadFile(filePath, response);
        } catch (FastDFSException e) {
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
    public FileResponseData deleteFile(String filePath) {
        FileResponseData responseData = new FileResponseData();
        try {
            fastDFSClient.deleteFile(filePath);
        } catch (FastDFSException e) {
            log.error("deleteFile error", e);
            responseData.setSuccess(false);
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        return responseData;
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
            String filepath = fastDFSClient.uploadFileWithMultipart(file);

            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(FastDFSClient.getFilenameSuffix(file.getOriginalFilename()));
            responseData.setHttpUrl(fileServerAddr + "/" + filepath);
        } catch (FastDFSException e) {
            responseData.setSuccess(false);
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }

}
