package cn.cyberpeace.mp.devops.cluster.cloud.platform.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
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
public class OpenFlowPageParams{

    @ApiModelProperty(value = "执行动作", required = true)
    @NotNull(message = "执行动作不能为空")
    private String method;

    /**
     * 每页条数
     */
    @Min(1)
    @ApiModelProperty(value = "每页条数",hidden = true)
    private Integer limit;

    /**
     * 页码，从1开始
     */
    @Min(1)
    @ApiModelProperty(value = "页码",hidden = true)
    private Integer offset;
}
