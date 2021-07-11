package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.ImageInjectFileDeleteRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


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
