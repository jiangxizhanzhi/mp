package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像构建请求信息
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
