package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName InstanceDeleteData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class InstanceDeleteData {
    @ApiModelProperty(value = "实例id")
    private String uuid;
    @ApiModelProperty(value = "镜像类型")
    private String imageType;
}