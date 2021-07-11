package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class FlowAttackConfigInfo {

    @ApiModelProperty("攻击类型列表")
    private List<FlowAttackType> types;

    @Data
    public static class FlowAttackType {
        @ApiModelProperty("攻击类型名称")
        private String name;

        @ApiModelProperty("攻击类型")
        private String value;

        @ApiModelProperty("攻击等级")
        private Integer severity;
    }

}
