package cn.cyberpeace.mp.devopscluster.service.upload;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.service.FileService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service
public class BaseUploadService extends BaseAbstractUploadService{

    
    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;

    
    ServiceResult serviceResult;

    BaseUploadService() {
    }

    public BaseUploadService build(MultipartFile file){
        this.file = file;
        return this;
    }

    
    @Override
    protected Boolean validUploadType(MultipartFile file) {
        if (Objects.isNull(file)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        List<String> supportUploadTypeList = this.supportUploadType();
        if (CollectionUtils.isEmpty(supportUploadTypeList)) {
            return Boolean.TRUE;
        }

        String originalFilename = file.getOriginalFilename();
        int index = StringUtils.isBlank(originalFilename) ? -1 : originalFilename.lastIndexOf(".");
        if (index < 0) {
            throw new MpRuntimeException(DevopsClusterErrorCode.CUSTOM_UPLOAD_TYPE_ERROR.format(StringUtils.join(supportUploadTypeList,",")));
        }

        String suffix = originalFilename.substring(index + 1);
        boolean anyMatch = supportUploadTypeList.stream().anyMatch(suffix::equals);
        if (!anyMatch) {
            throw new MpRuntimeException(DevopsClusterErrorCode.CUSTOM_UPLOAD_TYPE_ERROR.format(StringUtils.join(supportUploadTypeList,",")));
        }
        return Boolean.TRUE;
    }

    
    @Override
    protected List<String> supportUploadType() {
        return Lists.newArrayList();
    }

    
    @Override
    protected void uploadFile() {
        this.serviceResult = fileService.uploadFile(file);
    }

    
    protected ServiceResult getServiceResult(){
        return serviceResult;
    }

    
    public ServiceResult upload() {
        
        this.validUploadType(file);
        
        this.uploadFile();
        
        return this.getServiceResult();
    }

}
