package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.model.common.TypeItemModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeIdIntegerConvertModel {

    
    @ApiModelProperty(value = "ID")
    private Integer id;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "父ID")
    private Integer parentId;

    
    private List<TypeItemModel> child;

}
