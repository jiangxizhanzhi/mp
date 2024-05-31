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
}
