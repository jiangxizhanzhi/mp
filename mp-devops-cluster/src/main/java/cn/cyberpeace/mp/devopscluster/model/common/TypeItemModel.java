package cn.cyberpeace.mp.devopscluster.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeItemModel {

    
    @ApiModelProperty(value = "ID")
    private Integer id;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "父ID")
    private Integer parentId;

    
    @ApiModelProperty(value = "子节点属性")
    private List<TypeItemModel> child;
}
