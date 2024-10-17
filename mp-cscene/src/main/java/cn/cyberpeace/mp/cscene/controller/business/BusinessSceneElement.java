package cn.cyberpeace.mp.cscene.controller.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessSceneElement {
    @ApiModelProperty(value = "节点id")
    private String id;
    @ApiModelProperty(value = "节点名称")
    private String name;
    @ApiModelProperty(value = "节点类型")
    private Integer type;
    @ApiModelProperty(value = "cpu")
    private Integer cpu;
    @ApiModelProperty(value = "内存")
    private Integer ram;
    @ApiModelProperty(value = "磁盘")
    private Integer disk;
    @ApiModelProperty(value = "端口信息")
    private List<BusinessSceneElementPort> ports;
}

