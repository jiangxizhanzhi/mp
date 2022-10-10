package cn.cyberpeace.mp.flow.v2.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowQueryParams extends QueryParams {

    @ApiModelProperty(value = "流量类型")
    private List<Integer> categories;

    @ApiModelProperty(value = "资源类型(0 pcap包，1 脚本, 2 协议流量, 3 IXIA流量)")
    private List<Integer> resourceTypes;

    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
    private Integer interactiveType;

}
