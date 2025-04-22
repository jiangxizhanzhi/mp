package cn.cyberpeace.mp.cscene.request.load;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EvalCodeStageRsp {

    @ApiModelProperty("阶段id")
    private String code;

    @ApiModelProperty("阶段名称")
    private String name;
}

