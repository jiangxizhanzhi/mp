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
@ApiModel(value = "防火墙分页返回信息")
public class FireWallResponse {

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 入站策略
     */
    @ApiModelProperty("入站策略")
    private String ingress_name;

    /**
     * 出站策略
     */
    @ApiModelProperty("出站策略")
    private String engross_name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;


}
