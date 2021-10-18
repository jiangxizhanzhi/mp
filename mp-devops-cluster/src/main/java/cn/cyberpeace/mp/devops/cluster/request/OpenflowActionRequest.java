package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Description: 流表操作请求信息
 * @Author qiaokang
 * @Date：2021/7/9 14:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenflowActionRequest {

    @ApiModelProperty(value = "执行动作", required = true)
    @NotNull(message = "执行动作不能为空")
    private String method;

    @ApiModelProperty(value = "节点名称", required = true)
    @NotNull(message = "节点名称不能为空")
    private String hostname;

}
