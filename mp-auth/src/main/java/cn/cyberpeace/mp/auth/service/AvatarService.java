package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @Description: 头像管理service
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class AvatarService {

    public FileResponseData upload(MultipartFile file) {
        // 校验文件类型
        ServiceResult<Boolean> uploadResult = checkFileType(file);
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

    public ServiceResult<Boolean> checkFileType(MultipartFile file) {
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        // 校验文件后缀类型
        boolean suffixMatch = Arrays.stream(AuthConstant.UPLOAD_AVATAR_SUFFIX_TYPE).anyMatch(x -> x.equalsIgnoreCase(suffix));
        if (!suffixMatch) {
            return new ServiceResult<>(false, AuthErrorCode.AVATAR_MAGIC_HEADER_ERROR);
        }

        // 根据文件头判断文件类型
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
        String type = FileTypeUtil.getType(inputStream);
        boolean typeMatch = Arrays.stream(AuthConstant.UPLOAD_AVATAR_MAGIC_HEADER_TYPE).anyMatch(x -> x.equalsIgnoreCase(type));
        if (!typeMatch) {
            return new ServiceResult<>(false, AuthErrorCode.AVATAR_SUFFIX_TYPE_ERROR);
        }

        return new ServiceResult<>(true);
    }
}
