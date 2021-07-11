package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import cn.cyberpeace.mp.devopscluster.request.HypervisorRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorActionDTO extends BaseRequest {

    
    @ApiModelProperty(value = "节点名称",required = true)
    private String hostname;

    
    @ApiModelProperty(value = "操作(重启:reboot;关机:poweroff)",required = true)
    private String action;

    public HypervisorActionDTO(HypervisorRequest request){
        this.hostname = request.getHostname();
        this.action = request.getAction();
    }
}
