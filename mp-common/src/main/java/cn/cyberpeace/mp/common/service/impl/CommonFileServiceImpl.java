package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.impl.BaseRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.model.FileUploadResponse;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class CommonFileServiceImpl extends BaseRemoteFileServiceImpl {

    @Autowired
    public CommonFileServiceImpl(FdfsCommon fdfsCommon) {
        setFileClient(LfsCommon.lfsClient);
    }

    public ServiceResult uploadFile(InputStream inputStream, String fileName, String fileSuffix, String uploadType) {
        //检验文件格式
        switch (uploadType) {
            case "image":
                if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_IMAGE_LIST)) {
                    return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "不符合所需格式的文件").asResult();
                }
                break;
            case "attachment":
                if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_FILE_LIST)) {
                    return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "不符合所需格式的文件").asResult();
                }
                break;
            case "readMe":
                if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_FILE_README_LIST)) {
                    return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "不符合所需格式的文件").asResult();
                }
                break;
            default:
        }
        var uploadResult = super.upload(inputStream, fileSuffix);
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail()).asResult();
        } else {
            FileUploadResponse response = new FileUploadResponse(uploadResult.getSource(), fileName);
            return new ServiceResult<>(response).asResult();
        }
    }
}

