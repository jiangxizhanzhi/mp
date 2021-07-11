package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "网络分页返回信息")
public class NetworkPageVO extends SceneInfoVO {

    
    @ApiModelProperty("网络名称")
    private String name;

    
    @ApiModelProperty("网络类型")
    private String networkType;

    
    @ApiModelProperty("状态")
    private String status;

    
    @ApiModelProperty("IP地址")
    private List<Subnet> subnets;

    @ApiModelProperty("下行带宽(Kb/s)")
    private Long downloadSpeed;

    @ApiModelProperty("上行带宽(Kb/s)")
    private Long uploadSpeed;

    @ApiModelProperty("节点个数")
    private Integer nodeNum;

    
    @ApiModelProperty("创建时间")
    private String createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Subnet implements Serializable {

        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("子网CIDR")
        private String cidr;

        @ApiModelProperty("网关ip")
        private String gatewayIp;

        @ApiModelProperty("剩余IP数")
        private Integer freeIps;

        @ApiModelProperty("已使用IP数")
        private Integer usedIps;
    }
}
