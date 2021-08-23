package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
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
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
}
