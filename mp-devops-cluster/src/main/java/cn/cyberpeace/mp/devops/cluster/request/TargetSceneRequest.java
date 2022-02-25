package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetSceneRequest extends BasePageRequest implements Serializable {

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
    @ApiModelProperty("镜像类型")
    private ImageTypeEnum imageType;

    @ApiModelProperty("忽略用户Token")
    private Boolean ignoreUser;

    @ApiModelProperty("靶标分类，1：内置靶标，2：实体靶标，3：自定义靶标")
    private Integer targetCategory;

    @ApiModelProperty("是否模糊查询")
    private Boolean likeName = Boolean.TRUE;
}
