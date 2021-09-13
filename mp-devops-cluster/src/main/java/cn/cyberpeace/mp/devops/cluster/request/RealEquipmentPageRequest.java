package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实装分页请求信息")
public class RealEquipmentPageRequest extends BasePageRequest{

    /**
     * 系统主类型
     */
    @ApiModelProperty(value = "一级类型")
    private Integer category;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "二级类型")
    private Integer secondCategory;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "三级类型")
    private Integer thirdCategory;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 在位状态
     */
    @ApiModelProperty(value = "在位状态")
    private StatusEnum inPosition;

    /**
     * 使用状态
     */
    @ApiModelProperty(value = "使用状态")
    private StatusEnum useStatus;
}
