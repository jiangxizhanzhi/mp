package cn.cyberpeace.mp.datasandtable.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针脚本参数")
public class ProbeParamsRequest {
    private String probeId;

    @ApiModelProperty(value = "参数类型", required = true)
    private Integer paramType;

    @ApiModelProperty(value = "参数默认值", required = true)
    private String defaultVal;

    @ApiModelProperty(value = "参数描述", required = true)
    private String paramDes;

    @ApiModelProperty(value = "参数名称", required = true)
    private String paramName;
}
