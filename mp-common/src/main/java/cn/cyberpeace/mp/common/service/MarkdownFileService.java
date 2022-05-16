package cn.cyberpeace.mp.common.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MarkdownFileService extends BaseRemoteFileServiceImpl {

    @Autowired
    public MarkdownFileService(FdfsCommon fdfsCommon) {
        setFileClient(FdfsCommon.fdfsClient);
    }

    public ServiceResult<Boolean> validateUploadImageInput(InputStream is, String fileSuffix) {
        // TODO
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> validateUploadFileInput(InputStream is, String fileSuffix) {
        // TODO
        return new ServiceResult<>(true);
    }

    public ServiceResult<String> uploadImage(InputStream is, String fileSuffix) {
        var validateResult = validateUploadImageInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

    public ServiceResult<String> uploadFile(InputStream is, String fileSuffix) {
        var validateResult = validateUploadFileInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }

        return upload(is, fileSuffix);
    }

}
