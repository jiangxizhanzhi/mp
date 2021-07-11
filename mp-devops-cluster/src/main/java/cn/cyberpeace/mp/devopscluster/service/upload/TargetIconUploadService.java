package cn.cyberpeace.mp.devopscluster.service.upload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


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
