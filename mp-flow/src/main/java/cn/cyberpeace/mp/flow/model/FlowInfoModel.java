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
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_info")
public class FlowInfoModel extends BaseModel {

    
    @ApiModelProperty(value = "流量标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    
    @ApiModelProperty(value = "流量描述")
    @DefaultStr
    private String flowDesc;

    
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    @DefaultInt
    private Integer interactiveType;

    
    @ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
    @NotNull(message = "resourceType can not be empty")
    private Integer resourceType;

    
    @ApiModelProperty(value = "攻击脚本运行参数")
    @DefaultStr
    private String runCmd;

    
    @ApiModelProperty(value = "流量分类")
    @NotNull(message = "category can not be empty")
    private Integer category;

    
    @ApiModelProperty(value = "文件名称")
    @DefaultStr
    private String fileName;

    
    @ApiModelProperty(value = "pcap文件地址")
    @DefaultStr
    private String pcapFile;

    
    @ApiModelProperty(value = "脚本文件地址")
    @DefaultStr
    private String scriptFile;

}
