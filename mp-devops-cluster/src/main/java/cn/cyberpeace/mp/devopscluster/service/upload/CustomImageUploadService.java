 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomImageUploadService.java.
 // File size 1823.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.service.upload;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devopscluster.utils.FileUtils;
import cn.cyberpeace.mp.devopscluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName CustomImageUploadService
 * @Description 自定义上传镜像
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseUploadService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomImageUploadService extends BaseUploadService {

    /**
     * 文件上传客户端
     */
    private final ImagesService imagesService;

    public CustomImageUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return List.of("vmdk","raw","qcow2","vdi","zip","tar","bin");
    }

    @Override
    protected void uploadFile() {
        List<File> fileList = null;
        try {
            fileList = FileUtils.transferToFileList(Collections.singletonList(file));
        } catch (IOException e) {
            log.error("file transfer error",e);
        }
        BaseCloudPlatformResult model = imagesService.upload(fileList,"image");
        this.serviceResult = new Result(new TargetUploadVO(model.getTmpdirUuid(),model.getFileNames()));
    }
}
