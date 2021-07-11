package cn.cyberpeace.mp.devopscluster.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentPageRequest extends BasePageRequest{

    
    @ApiModelProperty(value = "一级类型")
    private Integer category;

    
    @ApiModelProperty(value = "二级类型")
    private Integer secondCategory;

    
    @ApiModelProperty(value = "三级类型")
    private Integer thirdCategory;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "在位状态")
    private Integer inPosition;

    
    @ApiModelProperty(value = "使用状态")
    private Integer useStatus;

    
    @ApiModelProperty(value = "精确查找名称")
    private String exactName;
}
