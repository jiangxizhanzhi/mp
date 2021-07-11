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
@Table("mp_flow_collection")
public class FlowCollectionModel extends BaseModel {

    
    @ApiModelProperty(value = "流量集合标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    
    @ApiModelProperty(value = "集合描述")
    @DefaultStr
    private String description;

    
    @ApiModelProperty(value = "流量集合类型")
    @NotNull(message = "category can not be empty")
    private Integer category;

    
    @ApiModelProperty(value = "流量集合分类")
    @NotNull(message = "typeFlag can not be empty")
    @DefaultInt(2)
    private Integer typeFlag;

    
    @ApiModelProperty(value = "背景流量数量")
    @DefaultInt
    private Integer backgroundFlowCount;

    
    @ApiModelProperty(value = "攻击流量数量")
    @DefaultInt
    private Integer attackFlowCount;

}
