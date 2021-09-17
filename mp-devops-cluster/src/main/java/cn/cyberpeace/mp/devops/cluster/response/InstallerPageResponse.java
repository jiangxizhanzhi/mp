package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "服务分页返回信息")
public class InstallerPageResponse extends BaseCloudPlatformResponse{

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 图标地址
     */
    @ApiModelProperty("图标地址")
    private String logo;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String categories;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;
}
