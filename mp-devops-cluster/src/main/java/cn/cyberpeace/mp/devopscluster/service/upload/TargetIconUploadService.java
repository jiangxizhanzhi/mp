package cn.cyberpeace.mp.devopscluster.service.upload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName TargetIconUploadService
 * @Description 靶标-图标上传方法
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseUploadService
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
