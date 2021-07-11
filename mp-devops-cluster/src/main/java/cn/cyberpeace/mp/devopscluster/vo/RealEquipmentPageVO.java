package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "实装分页返回信息")
public class RealEquipmentPageVO extends BaseVO {

    
    @ApiModelProperty(value = "ID")
    private String id;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "型号")
    private String model;

    
    @ApiModelProperty(value = "参数")
    private String params;

    
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    
    @ApiModelProperty(value = "一级分类名称")
    private String categoryName;


    
    @ApiModelProperty(value = "二级分类")
    private Integer subCategory;

    
    @ApiModelProperty(value = "二级分类名称")
    private String subCategoryName;


    
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty(value = "三级分类名称")
    private String thirdCategoryName;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "实体类型")
    private String entityType;

    
    @ApiModelProperty(value = "实体类型名称")
    private String entityTypeName;

    
    @ApiModelProperty(value = "实体详情")
    private String entityDetail;

    
    @ApiModelProperty(value = "在位状态,0:不在位，1：在位")
    private StatusEnum inPosition;

    
    @ApiModelProperty(value = "引用状态")
    private Integer referenceStatus;

    
    @ApiModelProperty(value = "使用状态，0：空闲，1：使用")
    private StatusEnum useStatus;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty("实体USB")
    private EntityUsb extEntityUsb;

    
    @ApiModelProperty("是否可登录")
    private Boolean loginEnable;
}
