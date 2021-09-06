package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 镜像分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "镜像分页返回信息")
public class ImagesPageResponse  extends BaseCloudPlatformResponse  {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("子分类")
    private Integer subCategory;

    /**
     * 虚拟类型：vm/container
     */
    @ApiModelProperty("虚拟类型：vm/container")
    private String imageType;

    /**
     * 操作系统类型
     */
    @ApiModelProperty("操作系统类型")
    private Integer systemType;

    /**
     * 大小
     */
    @ApiModelProperty("大小")
    private String flavor;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
}
