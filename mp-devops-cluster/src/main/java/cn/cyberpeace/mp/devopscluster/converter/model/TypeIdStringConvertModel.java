package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeIdStringConvertModel {

    
    @ApiModelProperty(value = "ID")
    private String id;

    
    @ApiModelProperty(value = "名称")
    private String name;

}
