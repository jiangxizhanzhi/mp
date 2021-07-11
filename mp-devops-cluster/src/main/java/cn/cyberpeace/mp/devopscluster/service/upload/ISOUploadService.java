package cn.cyberpeace.mp.devopscluster.service.upload;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ISOService;
import cn.cyberpeace.mp.devopscluster.utils.FileUtils;
import cn.cyberpeace.mp.devopscluster.vo.ISOUploadVO;
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
public class ISOUploadService extends BaseUploadService {

    
    private final ISOService isoService;

    @Override
    public ISOUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return List.of("iso");
    }

    @Override
    protected void uploadFile() {
        List<File> fileList = null;
        try {
            fileList = FileUtils.transferToFileList(Collections.singletonList(file));
        } catch (IOException e) {
            log.error("file transfer error", e);
        }
        BaseCloudPlatformResult model = isoService.upload(fileList);
        this.serviceResult = new Result(new ISOUploadVO(model.getTmpdirUuid(), model.getFileName()));
    }
}
