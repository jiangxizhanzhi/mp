package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesRebootInstanceRequest extends BaseRequest {


    
    @ApiModelProperty("实例ID")
    private String instanceId;

    
    @ApiModelProperty("镜像类型")
    private String imageType;
}
