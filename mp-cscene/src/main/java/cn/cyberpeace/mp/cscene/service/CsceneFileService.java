package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 */
@Service
public class CsceneFileService {

    private BaseRemoteFileService fileService;

    @Autowired
    public CsceneFileService(@Qualifier("lfsRemoteFileServiceImpl") BaseRemoteFileService fileService) {
        this.fileService = fileService;
    }

    public ServiceResult<String> upload(InputStream is) {
        return this.fileService.upload(is);
    }

    public ServiceResult<String> upload(InputStream is, String fileSuffix) {
        return this.fileService.upload(is, fileSuffix);
    }

    public ServiceResult<SceneFileUploadResponse> sceneUpload(InputStream is, String fileName, String fileSuffix, String... legalSuffix) {
        if (!new HashSet<>(Arrays.asList(legalSuffix)).contains(fileSuffix)) {
            return new ServiceResult<>(null, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        var uploadResult = fileService.upload(is, fileSuffix);
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail());
        } else {
            SceneFileUploadResponse response = new SceneFileUploadResponse(uploadResult.getSource(), fileName);
            return new ServiceResult<>(response);
        }
    }

}
