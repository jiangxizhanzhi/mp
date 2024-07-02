package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName FlowActuatorModel
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_actuator")
public class FlowActuatorModel extends BaseModel {

    /**
     * 执行器名称
     */
    @ApiModelProperty(value = "执行器名称")
    @DefaultStr
    private String title;

    /**
     * 执行器ip地址
     */
    @ApiModelProperty(value = "执行器ip地址")
    @NotBlank(message = "ip can not be empty")
    private String ip;

    /**
     * 网络id
     */
    @ApiModelProperty(value = "网络id")
    @DefaultStr
    private String networkId;

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @DefaultStr
    private String instanceId;

    /**
     * 运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
     */
    @ApiModelProperty(value = "流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过")
    private Integer status;
}
