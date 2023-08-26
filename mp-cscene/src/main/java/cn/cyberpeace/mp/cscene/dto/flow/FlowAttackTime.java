package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTime {
    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点ID")
    private String nodeId;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("次数")
    private Integer times;
}