package cn.cyberpeace.mp.devopscluster.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageDTO extends BasePageDTO {

    @ApiModelProperty(value = "执行动作", hidden = true)
    @NotNull(message = "执行动作不能为空")
    @Builder.Default
    private String method = "list";

}
