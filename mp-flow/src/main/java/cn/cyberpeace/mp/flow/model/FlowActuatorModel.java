package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_actuator")
public class FlowActuatorModel extends BaseModel {

    
    @ApiModelProperty(value = "执行器名称")
    @DefaultStr
    private String title;

    
    @ApiModelProperty(value = "执行器ip地址")
    @NotBlank(message = "ip can not be empty")
    private String ip;

    
    @ApiModelProperty(value = "网络id")
    @DefaultStr
    private String networkId;

    
    @ApiModelProperty(value = "实例id")
    @DefaultStr
    private String instanceId;

}
