package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName ImageInjectFileDeleteRequest
 * @Description 镜像文件注入请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageInjectFileDeleteRequest extends BaseRequest {

    @ApiModelProperty("文件ID")
    @NotBlank
    private String injectUuid;

    @ApiModelProperty("镜像ID")
    @NotBlank
    private String deviceUuid;
}
