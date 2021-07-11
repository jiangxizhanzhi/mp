package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "实装返回信息")
public class RealEquipmentByIdVO extends BaseVO {

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("图标")
    private String logo;

    
    @ApiModelProperty("角色")
    private Integer role;

    
    @ApiModelProperty("分类")
    private Integer mainCategory;

    
    @ApiModelProperty("子分类")
    private Integer subCategory;

    
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo systemInfo;

    
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    
    @ApiModelProperty("实体USB")
    private EntityUsb extEntityUsb;

    
    private ExtGateway extGateway;

    
    @ApiModelProperty("交换机接入配置")
    private List<ExtEntityAccess> extEntityAccess;

    
    @ApiModelProperty("描述")
    private String description;



}
