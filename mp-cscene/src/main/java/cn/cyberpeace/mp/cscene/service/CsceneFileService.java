package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;


@Service
public class CsceneFileService extends BaseRemoteFileServiceImpl {

    @Autowired
    public CsceneFileService(FsCommon fsCommon) {
        setFileClient(FsCommon.fileClient);
    }

    public ServiceResult<SceneFileUploadResponse> sceneUpload(InputStream is, String fileName, String fileSuffix, String... legalSuffix) {
        if (!new HashSet<>(Arrays.asList(legalSuffix)).contains(fileSuffix)) {
            return new ServiceResult<>(null, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        var uploadResult = super.upload(is, fileSuffix);
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail());
        } else {
            SceneFileUploadResponse response = new SceneFileUploadResponse(uploadResult.getSource(), fileName);
            return new ServiceResult<>(response);
        }
    }

    public ServiceResult<SceneFileUploadResponse> sceneUpload(InputStream is,String filename) {
        var uploadResult = super.upload(is,filename);
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail());
        } else {
            SceneFileUploadResponse response = new SceneFileUploadResponse(uploadResult.getSource(), filename);
            return new ServiceResult<>(response);
        }
    }

}
