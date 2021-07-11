package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "系统运维-实装管理详情返回信息")
public class RealEquipmentDevopsDetailVO extends BaseVO {

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("图标")
    private String logo;

    
    @ApiModelProperty("管理地址")
    private List<String> address;

    
    @ApiModelProperty("型号")
    private String model;

    
    @ApiModelProperty(value = "参数")
    private String params;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    
    @ApiModelProperty("实体USB")
    private EntityUsb extEntityUsb;

    
    @ApiModelProperty("CPU使用率")
    private BigDecimal cpuPercent;

    
    @ApiModelProperty("内存使用率")
    private BigDecimal ramPercent;

    
    @ApiModelProperty("磁盘使用率")
    private BigDecimal diskPercent;


    
    @ApiModelProperty("登录链接信息")
    private List<TargetConnection> connections;
}
