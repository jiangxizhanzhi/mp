package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.common.FileCheckCommon;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Slf4j
public class AvatarService {

    public FileResponseData upload(MultipartFile file) {
        
        FileCheckRequest fileCheckRequest = new FileCheckRequest();
        fileCheckRequest.setMaxSize(AuthConstant.UPLOAD_AVATAR_MAX_SIZE);
        fileCheckRequest.setSuffixList(AuthConstant.UPLOAD_AVATAR_SUFFIX_TYPE);
        fileCheckRequest.setMagicHeaderList(AuthConstant.UPLOAD_AVATAR_MAGIC_HEADER_TYPE);
        ServiceResult<Boolean> uploadResult = FileCheckCommon.checkFile(file,fileCheckRequest);
        if (!Boolean.TRUE.equals(uploadResult.getSource())) {
            throw new MpRuntimeException(uploadResult.getServiceCode());
        }
        FileResponseData responseData = new FileResponseData();
        try {
            
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            String filepath = FsCommon.upload(file.getInputStream(), suffix);
            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(suffix);
            responseData.setHttpUrl(FsCommon.genHttpUrl(filepath));
            responseData.setInnerHttpUrl(FsCommon.genInnerHttpUrl(filepath));
        } catch (MpRuntimeException | IOException e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }
}
