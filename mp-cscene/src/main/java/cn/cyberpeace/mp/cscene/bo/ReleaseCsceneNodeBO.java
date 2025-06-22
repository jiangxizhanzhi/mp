package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "场景模板数据")
@Data
public class ReleaseCsceneNodeBO implements Serializable {
    @ApiModelProperty(value = "设备ID")
    private String numberId;
    @ApiModelProperty(value = "设备名称")
    private String numberName;
    @ApiModelProperty(value = "设备类型 交换机/防火墙/终端/路由 终端")
    private Integer nodeType;
    @ApiModelProperty(value = "所属区域-XX所")
    private String areaName;
}
