package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.common.constant.FileSuffixConstant;
import cn.cyberpeace.mp.common.service.MarkdownFileService;
import cn.cyberpeace.mp.common.util.SuffixCheckUtil;
import cn.cyberpeace.mp.cscene.repository.mapper.CloudSceneMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudSceneServiceImpl {

    @Autowired
    private CloudSceneMapper cloudSceneMapper;

    @Autowired
    private MarkdownFileService markdownFileService;

    //图片上传
    public ServiceResult<String> uploadImage(InputStream is, String fileSuffix) {
        var validateResult = validateUploadImageInput(is, fileSuffix);
        if (!Boolean.TRUE.equals(validateResult.getSource())) {
            return new ServiceResult<>(null, validateResult.getServiceCode(), validateResult.getDetail());
        }
        return markdownFileService.upload(is, fileSuffix);
    }

    public ServiceResult<Boolean> validateUploadImageInput(InputStream is, String fileSuffix) {
        //上传图片支持类型校验
        if (!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_IMAGE_LIST)) {
            return new ServiceResult<>(false, CommonErrorCode.UPLOAD_FILE_TYPE_ERROR);
        }
        return new ServiceResult<>(true);
    }

    //文件上传
    public ServiceResult<String> uploadFile(InputStream is, String fileSuffix, String uploadType) {
        //检验文件格式
        switch (uploadType) {
            case "attachment":
                if(!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_FILE_LIST)){
                    return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "不符合所需格式的文件").asResult();
                }
                break;
            case "readMe":
                if(!SuffixCheckUtil.checkBySuffix(fileSuffix, FileSuffixConstant.LOAD_UPLOAD_FILE_README_LIST)){
                    return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "不符合所需格式的文件").asResult();
                }
                break;
            default:
        }
        return markdownFileService.upload(is, fileSuffix);
    }

    public String getFileNameByRelativeUrl(String fileUrl) {
        File file = FileUtil.file(fileUrl);
        System.out.println(file.getName());
        System.out.println(file.length());
        return null;
    }
}

