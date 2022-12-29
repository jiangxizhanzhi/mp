package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class VlanIpStockVO implements Serializable {

    @ApiModelProperty("vlan总数")
    private Integer totalVlans;

    @ApiModelProperty("已使用vlan")
    private Integer usedVlans;

    @ApiModelProperty("IP总数")
    private Integer totalIps;

    @ApiModelProperty("已使用IP")
    private Integer usedIps;

}
