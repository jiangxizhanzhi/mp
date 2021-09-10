package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
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
public class RealEquipmentPageResponse  extends BaseCloudPlatformResponse{

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号")
    private String model;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String params;

    /**
     * 分类
     */
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    /**
     * 分类
     */
    @ApiModelProperty(value = "一级分类名称")
    private String categoryName;


    /**
     * 子分类
     */
    @ApiModelProperty(value = "二级分类")
    private Integer subCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "二级分类名称")
    private String subCategoryName;


    /**
     * 子分类
     */
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    /**
     * 三级分类名称
     */
    @ApiModelProperty(value = "三级分类名称")
    private String thirdCategoryName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 实体类型
     */
    @ApiModelProperty(value = "实体类型")
    private String entityType;

    /**
     * 实体类型名称
     */
    @ApiModelProperty(value = "实体类型名称")
    private String entityTypeName;

    /**
     * 实体详情
     */
    @ApiModelProperty(value = "实体详情")
    private String entityDetail;

    /**
     * 在位状态
     */
    @ApiModelProperty(value = "在位状态,0:不再在位，1：在位")
    private StatusEnum inPosition;

    /**
     * 引用状态
     */
    @ApiModelProperty(value = "引用状态")
    private Integer referenceStatus;

    /**
     * 使用状态
     */
    @ApiModelProperty(value = "使用状态，0：空闲，1：使用")
    private StatusEnum useStatus;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;
}
