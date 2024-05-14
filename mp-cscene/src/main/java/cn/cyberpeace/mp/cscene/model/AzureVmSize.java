package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_azure_vm_size")
public class AzureVmSize extends BaseModel {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "cpu")
    private Integer vcpu;
    @ApiModelProperty(value = "内存")
    private Integer ram;
    @ApiModelProperty(value = "最多可用数据盘数")
    private Integer maxDataDisks;
    @ApiModelProperty(value = "最多可用网卡数")
    private Integer maxNics;
    @ApiModelProperty(value = "成本/月")
    private String costMonth;
}