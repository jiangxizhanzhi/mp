package cn.cyberpeace.mp.devopscluster.service.upload;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class InjectFileUploadService extends BaseUploadService {

    public InjectFileUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return Lists.newArrayList();
    }
}
