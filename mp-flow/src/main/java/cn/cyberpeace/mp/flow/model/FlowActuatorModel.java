// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowActuatorModel.java.
// File size 1503.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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
    @DefaultStr
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
    @ApiModelProperty(value = "发生器云资源id")
    @DefaultStr
    private String resourceId;

    /**
     * 运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
     */
    @ApiModelProperty(value = "流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过")
    @DefaultInt
    private Integer status;

    @ApiModelProperty(value = "创建发生器时关联的场景模板id")
    @DefaultStr
    private String csceneId;

    @ApiModelProperty(value = "创建发生器时关联的场景实例id")
    @DefaultStr
    private String csceneInstanceId;
}
