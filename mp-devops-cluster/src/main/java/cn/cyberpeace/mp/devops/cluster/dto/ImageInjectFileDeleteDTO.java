package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.ImageInjectFileDeleteRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 镜像文件注入请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageInjectFileDeleteDTO extends BaseDTO {

    @ApiModelProperty("文件ID")
    @NotBlank
    private String injectUuid;

    @ApiModelProperty("镜像ID")
    @NotBlank
    private String deviceUuid;

    public ImageInjectFileDeleteDTO(ImageInjectFileDeleteRequest request){
        this.deviceUuid = request.getDeviceUuid();
        this.injectUuid = request.getInjectUuid();
    }
}
