package cn.cyberpeace.mp.devops.cluster.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SshPrivateKeyUploadService extends BaseUploadService {

    public SshPrivateKeyUploadService build(MultipartFile file) {
        this.file = file;
        return this;
    }

    @Override
    protected List<String> supportUploadType() {
        return List.of("pub");
    }
}
