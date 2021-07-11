package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


public interface FileService {

    ServiceResult<FileResponseData> uploadFile(MultipartFile file);

    void downloadFile(String filePath, HttpServletResponse response) throws MpRuntimeException;

    ServiceResult deleteFile(String filePath);


}
