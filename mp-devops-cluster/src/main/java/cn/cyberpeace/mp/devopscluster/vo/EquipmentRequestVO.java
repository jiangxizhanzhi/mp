package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取边界网络设备的请求参数格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取边界网络设备的请求参数格式")
public class EquipmentRequestVO {

    @ApiModelProperty(value = "pageNum")
    private Integer page;

    @ApiModelProperty(value = "pageSize")
    private Integer perPage;
}
