package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.FileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 基础的上传方法
 */
@Service
public class BaseUploadService extends BaseAbstractUploadService{

    /**
     * 文件上传客户端
     */
    @Qualifier("fastDfsFileServiceImpl")
    @Autowired
    private FileService fileService;

    /**
     * 存放上传文件后的返回结果
     */
    ServiceResult serviceResult;

    BaseUploadService() {
    }

    public BaseUploadService build(MultipartFile file){
        this.file = file;
        return this;
    }

    /**
     * 验证上传文件格式
     *
     * @param file
     * @return
     */
    @Override
    protected Boolean validUploadType(MultipartFile file) {
        List<String> supportUploadTypeList = this.supportUploadType();
        if (CollectionUtils.isEmpty(supportUploadTypeList)) {
            return Boolean.TRUE;
        }

        String originalFilename = file.getOriginalFilename();
        int index = StringUtils.isBlank(originalFilename) ? -1 : originalFilename.lastIndexOf(".");
        if (index < 0) {
            throw new MpRuntimeException(DevopsClusterErrorCode.CUSTOM_UPLOAD_TYPE_ERROR);
        }

        String suffix = originalFilename.substring(index + 1);
        boolean anyMatch = supportUploadTypeList.stream().anyMatch(suffix::equals);
        if (!anyMatch) {
            throw new MpRuntimeException(DevopsClusterErrorCode.CUSTOM_UPLOAD_TYPE_ERROR);
        }
        return Boolean.TRUE;
    }

    /**
     * 默认支持所有类型
     * @return
     */
    @Override
    protected List<String> supportUploadType() {
        return Lists.newArrayList();
    }

    /**
     * 默认上传到fastDFS文件服务器
     */
    @Override
    protected void uploadFile() {
        this.serviceResult = fileService.uploadFile(file);
    }

    /**
     * 获取上传文件后的返回结果
     * @return
     */
    protected ServiceResult getServiceResult(){
        return serviceResult;
    }

    /**
     * 上传文件
     */
    public ServiceResult upload() {
        //验证上传文件格式
        this.validUploadType(file);
        //上传文件
        this.uploadFile();
        //获取返回结果
        return this.getServiceResult();
    }

}
