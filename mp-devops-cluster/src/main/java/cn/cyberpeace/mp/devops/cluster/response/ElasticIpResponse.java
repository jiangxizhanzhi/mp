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
@ApiModel(value = "弹性IP分页返回信息")
public class ElasticIpResponse extends BaseCloudPlatformResponse {

    /**
     * 固定IP
     */
    @ApiModelProperty("固定IP")
    private String fixedIpAddress;

    /**
     * 弹性IP
     */
    @ApiModelProperty("弹性IP")
    private String floatIpAddress;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createdAt;
}
