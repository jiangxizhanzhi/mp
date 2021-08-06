package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件操作接口
 */
public interface FileService {

    ServiceResult uploadFile(MultipartFile file, HttpServletRequest request);

    void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException;

    ServiceResult deleteFile(String filePath);


}
