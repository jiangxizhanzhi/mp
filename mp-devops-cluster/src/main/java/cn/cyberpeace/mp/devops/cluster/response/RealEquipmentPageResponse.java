package cn.cyberpeace.mp.devops.cluster.response;

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
    @ApiModelProperty(value = "分类")
    private Integer category;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private Integer subCategory;

    /**
     * 系统信息
     */
    @ApiModelProperty(value = "系统信息")
    private String systemInfo;

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
     * 实体详情
     */
    @ApiModelProperty(value = "实体详情")
    private String entityDetail;

    /**
     * 在位状态
     */
    @ApiModelProperty(value = "在位状态")
    private Boolean status;
}
