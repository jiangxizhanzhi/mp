package cn.cyberpeace.mp.devops.cluster.service.impl;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 基础的上传方法
 */
public abstract class BaseAbstractUploadService {

    /**
     * 文件流
     */
    MultipartFile file;

    BaseAbstractUploadService() {
    }

    /**
     * 验证上传文件格式
     *
     * @param file
     * @return
     */
    protected abstract Boolean validUploadType(MultipartFile file);

    /**
     * 支持的上传文件类型
     *
     * @return
     */
    protected abstract List<String> supportUploadType();

    /**
     * 上传文件
     *
     * @return
     */
    protected abstract void uploadFile();

}
