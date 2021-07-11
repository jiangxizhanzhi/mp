package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像删除实例请求信息")
public class ImagesDeleteInstanceRequest extends BaseRequest {


    
    @ApiModelProperty(value = "UUID")
    private String uuid;

    
    @ApiModelProperty(value = "镜像名称")
    private String imageType;

}
