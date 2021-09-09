package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "靶标分页请求信息")
public class TargetPageRequest extends BasePageRequest{

    /**
     * 系统主类型
     */
    @ApiModelProperty(value = "系统一级类型")
    private Integer category;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "系统二级类型")
    private Integer secondCategory;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "vm,container,entity,simple")
    private ImageTypeEnum imageType;
}
