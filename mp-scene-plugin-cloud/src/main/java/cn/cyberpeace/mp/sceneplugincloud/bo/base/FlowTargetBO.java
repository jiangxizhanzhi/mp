package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowTargetBO {

    @ApiModelProperty(value = "源节点")
    private Target source;

    @ApiModelProperty(value = "目标节点")
    private Target target;

    @Getter
    @Setter
    public static class Target {
        @ApiModelProperty(value = "节点id")
        private String nodeId;
        @ApiModelProperty(value = "节点名称")
        private String name;
    }
}
