package cn.cyberpeace.mp.devopscluster.service.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public abstract class BaseAbstractUploadService {

    
    MultipartFile file;

    BaseAbstractUploadService() {
    }

    
    protected abstract Boolean validUploadType(MultipartFile file);

    
    protected abstract List<String> supportUploadType();

    
    protected abstract void uploadFile();

}
