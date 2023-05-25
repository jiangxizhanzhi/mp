package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 节点实时速率对象
 * @Author: guquan
 * @Date：2023/5/20 16:11
 */
@Data
@ApiModel("节点实时速率对象")
public class FlowNodeTxBps {

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 实时发送速率(单位byte/s)
     */
    @ApiModelProperty(value = "实时发送速率(单位byte/s)")
    private Long txBps;

}
