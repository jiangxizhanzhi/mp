package cn.cyberpeace.mp.devops.cluster.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 靶标-图标上传方法
 */
@Service
public class TargetIconUploadService extends BaseUploadService {

    public TargetIconUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return List.of("jpeg","png","jpg");
    }
}
