package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @ClassName CsceneFileService
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 * @Version 1.0.0
 * @see BaseRemoteFileServiceImpl
 */
@Service
public class CsceneFileService extends BaseRemoteFileServiceImpl {

    /**
     * sceneUpload
     *
     * @param is
     * @param fileName
     * @param fileSuffix
     * @param legalSuffix
     * @return
     */
    public ServiceResult<SceneFileUploadResponse> sceneUpload(InputStream is, String fileName, String fileSuffix, String... legalSuffix) {
        if (!new HashSet<>(Arrays.asList(legalSuffix)).contains(fileSuffix)) {
            return new ServiceResult<>(null, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        ServiceResult<String> uploadResult = super.upload(is, fileSuffix);
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail());
        } else {
            SceneFileUploadResponse response = new SceneFileUploadResponse(uploadResult.getSource(), fileName);
            return new ServiceResult<>(response);
        }
    }

}
