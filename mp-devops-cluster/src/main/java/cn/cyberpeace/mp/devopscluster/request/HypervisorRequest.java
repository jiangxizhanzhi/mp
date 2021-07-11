package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorRequest extends BaseRequest {

    
    @ApiModelProperty(value = "节点名称",required = true)
    private String hostname;

    
    @ApiModelProperty(value = "操作(重启:reboot;关机:poweroff)",required = true)
    private String action;
}
