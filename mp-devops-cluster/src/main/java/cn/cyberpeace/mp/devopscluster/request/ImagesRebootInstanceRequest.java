package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImagesRebootInstanceRequest
 * @Description 镜像构建请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesRebootInstanceRequest extends BaseRequest {


    /**
     * 实例ID
     */
    @ApiModelProperty("实例ID")
    private String instanceId;

    /**
     * 镜像类型
     */
    @ApiModelProperty("镜像类型")
    private String imageType;
}
