package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "系统运维-实装管理场景引用返回信息")
public class RealEquipmentScenesPageResponse extends BaseCloudPlatformResponse{

    /**
     * 应用场景
     */
    @ApiModelProperty("应用场景")
    private String scenesName;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 用户
     */
    @ApiModelProperty("用户")
    private String user;

}
