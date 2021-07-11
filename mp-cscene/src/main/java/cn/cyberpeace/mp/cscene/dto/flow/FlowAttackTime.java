package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTime {
    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("次数")
    private Integer times;
}