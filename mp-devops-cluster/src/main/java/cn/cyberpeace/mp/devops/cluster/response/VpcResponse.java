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
@ApiModel(value = "VPC分页返回信息")
public class VpcResponse {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String ip;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String state;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createAt;


}
