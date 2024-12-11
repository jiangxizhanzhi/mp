 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowActuatorResponse.java.
 // File size 1882.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

 /**
 * @ClassName FlowActuatorResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量发生器返回信息")
@Builder
public class FlowActuatorResponse implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "仪表名称")
    private String name;

    /**
     * 场景名称
     */
    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    private String ip;

    /**
     * 目标子网
     */
    @ApiModelProperty(value = "目标子网")
    private String nics;

    /**
     * 运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
     */
    @ApiModelProperty(value = "流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
