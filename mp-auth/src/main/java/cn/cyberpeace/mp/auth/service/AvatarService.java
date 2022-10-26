package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.common.FileCheckCommon;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: 头像管理service
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class AvatarService {

    public FileResponseData upload(MultipartFile file) {
        // 校验文件大小、后缀、魔术头
        ServiceResult<Boolean> uploadResult = FileCheckCommon.checkFile(file,
                AuthConstant.UPLOAD_AVATAR_MAX_SIZE,
                AuthConstant.UPLOAD_AVATAR_SUFFIX_TYPE,
                AuthConstant.UPLOAD_AVATAR_MAGIC_HEADER_TYPE);
        if (!Boolean.TRUE.equals(uploadResult.getSource())) {
            throw new MpRuntimeException(uploadResult.getServiceCode());
        }
        FileResponseData responseData = new FileResponseData();
        try {
            // 上传到服务器
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            String filepath = FdfsCommon.upload(file.getInputStream(), suffix);
            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(suffix);
            responseData.setHttpUrl(FdfsCommon.genHttpUrl(filepath));
            responseData.setInnerHttpUrl(FdfsCommon.genInnerHttpUrl(filepath));
        } catch (MpRuntimeException | IOException e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }
}
