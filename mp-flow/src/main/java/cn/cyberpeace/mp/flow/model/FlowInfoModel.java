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
@Table("mp_flow_flow_info")
public class FlowInfoModel extends BaseModel {

    /**
     * 流量标题
     */
    @ApiModelProperty(value = "流量标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    /**
     * 流量描述
     */
    @ApiModelProperty(value = "流量描述")
    @DefaultStr
    private String flowDesc;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    @DefaultStr
    private int interactiveType;

    /**
     * 资源类型：0 pcap包，1 脚本
     */
    @ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
    @NotBlank(message = "resourceType can not be empty")
    private int resourceType;

    /**
     * pcap文件地址
     */
    @ApiModelProperty(value = "pcap文件地址")
    @DefaultStr
    private String pcapFile;

    /**
     * 脚本文件地址
     */
    @ApiModelProperty(value = "脚本文件地址")
    @DefaultStr
    private String scriptFile;

}
