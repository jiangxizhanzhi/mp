package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.utils.FileUtils;
import cn.cyberpeace.mp.devops.cluster.vo.TargetUploadVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 服务管理-Docker文件上传
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InstallerDockerUploadService extends BaseUploadService {

    /**
     * 文件上传客户端
     */
    private final ImagesService imagesService;

    public InstallerDockerUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return List.of("tar","zip");
    }

    @Override
    protected void uploadFile() {
        Map<String, File> fileList = null;
        try {
            fileList = FileUtils.transferToFileMap(Collections.singletonList(file));
        } catch (IOException e) {
            log.error("file transfer error",e);
        }
        BaseCloudPlatformResult model = imagesService.upload(fileList,"installer");
        if (Objects.isNull(model) || !model.isSuccess()){
            this.serviceResult = ServiceResult.fail("file upload error");
        }
        this.serviceResult = new Result(new TargetUploadVO(model.getTmpdirUuid(),model.getFileNames()));
    }
}
