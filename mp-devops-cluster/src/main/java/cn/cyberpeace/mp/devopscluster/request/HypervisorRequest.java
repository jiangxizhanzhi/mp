package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName HypervisorRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorRequest extends BaseRequest {

    /**
     * 主机名/节点名称
     */
    @ApiModelProperty(value = "节点名称",required = true)
    private String hostname;

    /**
     * 操作：重启reboot、关机poweroff
     */
    @ApiModelProperty(value = "操作(重启:reboot;关机:poweroff)",required = true)
    private String action;
}
