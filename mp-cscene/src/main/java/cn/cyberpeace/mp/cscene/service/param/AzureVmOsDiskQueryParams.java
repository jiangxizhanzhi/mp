package cn.cyberpeace.mp.cscene.service.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AzureVmOsDiskQueryParams {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "虚拟机系统类型")
    private String vmType;
    @ApiModelProperty(value = "云类型")
    private String cloudType;
    @ApiModelProperty(value = "分页页码")
    private Integer page;
    /** 分页每页条数 */
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;
}
