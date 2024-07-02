package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.response.FileUploadResponse;
import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 通用的远程文件上传controller
 * @param <S> 远程文件操作service
 */
@Deprecated
public interface BaseRemoteFileUploadController<S extends BaseRemoteFileService> {

    /**
     * getRemoteFileService
     *
     * @return
     */
    S getRemoteFileService();

    /**
     * toFileUploadResponse
     *
     * @param id
     * @param responseClazz
     * @return
     */
    static <T extends FileUploadResponse> T toFileUploadResponse(String id, Class<T> responseClazz) {
        T response = ClassUtils.createInstance(responseClazz);
        response.setId(id);
        return response;
    }

    /**
     * toFileUploadResponseResult
     *
     * @param uploadResult
     * @param responseClazz
     * @return
     */
    static <T extends FileUploadResponse> ServiceResult<T> toFileUploadResponseResult(
            ServiceResult<String> uploadResult, Class<T> responseClazz) {
        if (!uploadResult.judgeSuccess()) {
            return new ServiceResult<>(null, uploadResult.getServiceCode(), uploadResult.getDetail());
        }

        return new ServiceResult<>(toFileUploadResponse(uploadResult.getSource(), responseClazz),
                uploadResult.getServiceCode(), uploadResult.getDetail());
    }

    /**
     * 通用上传接口
     *
     * @param file
     * @return 文件id
     */
    @ApiOperation("上传文件")
    @PostMapping("/file_upload")
    default ServiceResult<String> upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return getRemoteFileService().upload(file.getInputStream(),
                FileUtil.getSuffix(file.getOriginalFilename())).asResult();
    }

}
