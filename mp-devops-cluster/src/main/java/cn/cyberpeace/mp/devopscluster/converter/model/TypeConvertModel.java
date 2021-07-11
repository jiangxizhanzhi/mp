package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TypeConvertModel {

    
    @ApiModelProperty("类型ID")
    private Integer id;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("2级类型ID")
    private Integer l2ParentId;

    
    @ApiModelProperty("1级类型ID")
    private Integer l1ParentId;

}
