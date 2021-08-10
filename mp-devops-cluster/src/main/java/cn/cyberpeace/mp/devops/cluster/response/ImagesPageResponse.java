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
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private String category;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 系统类型
     */
    @ApiModelProperty(value = "系统类型")
    private String systemType;

    /**
     * 大小
     */
    @ApiModelProperty(value = "大小")
    private String size;

    /**
     * 文件注入
     */
    @ApiModelProperty(value = "文件注入")
    private Object injectFiles;

    /**
     * 授权人数
     */
    @ApiModelProperty(value = "授权")
    private Long authNums;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;
}
