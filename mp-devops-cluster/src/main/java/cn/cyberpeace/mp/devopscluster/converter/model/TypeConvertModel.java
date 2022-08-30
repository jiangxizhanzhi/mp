package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TypeConvertModel {

    /**
     * 类型ID
     */
    @ApiModelProperty("类型ID")
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 2级类型ID
     */
    @ApiModelProperty("2级类型ID")
    private Integer l2ParentId;

    /**
     * 1级类型ID
     */
    @ApiModelProperty("1级类型ID")
    private Integer l1ParentId;

}
