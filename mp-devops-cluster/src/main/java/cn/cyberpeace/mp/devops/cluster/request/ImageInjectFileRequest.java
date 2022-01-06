package cn.cyberpeace.mp.devops.cluster.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 镜像文件注入请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageInjectFileRequest extends BaseRequest {

    @NotBlank
    private String deviceUuid;

    @NotBlank
    private String fileUrl;

    @NotBlank
    private String fileName;

    @NotNull
    private Long fileSize;
}
