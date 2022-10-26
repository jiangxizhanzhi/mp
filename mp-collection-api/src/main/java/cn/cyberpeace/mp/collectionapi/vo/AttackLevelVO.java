package cn.cyberpeace.mp.collectionapi.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackLevelVO {
    @ApiModelProperty(value = "级别")
    private String severity;

    @ApiModelProperty(value = "数量节点")
    private List<Count> counts;

    @Data
    public static class Count{
        @ApiModelProperty(value = "数量")
        private long num;

        @ApiModelProperty(value = "时间")
        private String time;
    }
}
