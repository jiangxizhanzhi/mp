package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "更新流量集合请求")
public class ModifyCollectionRequest {

    
    @ApiModelProperty(required = true, value = "流量集合id")
    @NotBlank(message = "id can not be empty")
    private String id;

    
    @ApiModelProperty(value = "流量集合标题")
    private String title;

    
    @ApiModelProperty(value = "集合描述")
    private String description;

    
    @ApiModelProperty(value = "流量集合类型")
    private Integer category;

    
    @ApiModelProperty(value = "流量集合分类")
    @DefaultInt(2)
    private Integer typeFlag;

    
    @ApiModelProperty(value = "背景流量数量")
    private Integer backgroundFlowCount;

    
    @ApiModelProperty(value = "攻击流量数量")
    private Integer attackFlowCount;

    
    @ApiModelProperty(value = "流量信息")
    private List<FlowInfoModel> flowInfoList;

}
