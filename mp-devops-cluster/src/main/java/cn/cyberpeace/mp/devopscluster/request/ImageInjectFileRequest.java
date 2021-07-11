package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageInjectFileRequest extends BaseRequest {

    @NotBlank
    @ApiModelProperty(value = "设备ID")
    private String deviceUuid;

    @NotBlank
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @NotBlank
    @ApiModelProperty(value = "文件名")
    private String fileName;

    @NotNull
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;
}
