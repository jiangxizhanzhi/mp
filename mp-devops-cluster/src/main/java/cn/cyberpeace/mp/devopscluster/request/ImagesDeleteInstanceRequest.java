package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImagesDeleteInstanceRequest
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
@ApiModel(value = "镜像删除实例请求信息")
public class ImagesDeleteInstanceRequest extends BaseRequest {


    /**
     * 名称
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

    /**
     * 镜像名称
     */
    @ApiModelProperty(value = "镜像名称")
    private String imageType;

}
