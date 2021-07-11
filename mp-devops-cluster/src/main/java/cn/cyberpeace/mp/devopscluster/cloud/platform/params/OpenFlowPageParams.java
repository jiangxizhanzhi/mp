package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageParams{

    @ApiModelProperty(value = "执行动作", required = true)
    @NotNull(message = "执行动作不能为空")
    private String method;

    
    @Min(1)
    @ApiModelProperty(value = "每页条数",hidden = true)
    private Integer limit;

    
    @Min(1)
    @ApiModelProperty(value = "页码",hidden = true)
    private Integer offset;
}
