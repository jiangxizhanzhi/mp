 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserResourceResponse.java.
 // File size 787.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName UserResourceResponse
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 11:09
 * @Version 1.0.0
 */
@Data
@Builder
@ApiModel("用户资源")
public class UserResourceResponse {

    @ApiModelProperty("容器数量")
    private Integer containerCount;

    @ApiModelProperty("实装设备数量")
    private Integer entityCount;

    @ApiModelProperty("场景数量")
    private Integer sceneCount;

    @ApiModelProperty("虚拟机数量")
    private Integer vmCount;

    @ApiModelProperty("dynamips数量")
    private Integer dynamips;

}
