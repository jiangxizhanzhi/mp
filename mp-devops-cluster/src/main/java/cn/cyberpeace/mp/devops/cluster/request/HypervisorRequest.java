package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "节点管理-重启/关机")
public class HypervisorRequest extends BaseRequest {

    /**
     * 主机名/节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    @ApiModelProperty(value = "节点名称",required = true)
    private String hostname;

    /**
     * 操作：重启reboot、关机poweroff
     */
    @NotBlank(message = "操作action不能为空")
    @ApiModelProperty(value = "操作(重启:reboot;关机:poweroff)",required = true)
    private String action;
}
