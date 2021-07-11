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


@Service
@Slf4j
@RequiredArgsConstructor
public class InstallerDockerUploadService extends BaseUploadService {

    
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
        List<File> fileList = null;
        try {
            fileList = FileUtils.transferToFileList(Collections.singletonList(file));
        } catch (IOException e) {
            log.error("file transfer error",e);
        }
        BaseCloudPlatformResult model = imagesService.upload(fileList,"installer");
        this.serviceResult = new Result(new TargetUploadVO(model.getTmpdirUuid(),model.getFileNames()));
    }
}
