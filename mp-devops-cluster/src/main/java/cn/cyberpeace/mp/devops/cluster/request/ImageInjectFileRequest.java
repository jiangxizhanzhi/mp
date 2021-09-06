package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 镜像文件注入请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像文件注入请求信息")
public class ImageInjectFileRequest extends BaseRequest {

    /**
     * UUID
     */
    private String uuid;


    /**
     * 文件流
     */
    private MultipartFile file;
}
