package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MultiSceneCommonTargetsQueryParams implements SnakeHttpParam {
    /**
     * 镜像类型
     */
    @ApiModelProperty("镜像类型")
    private ImageTypeEnum imageType;

    @ApiModelProperty("角色")
    private Integer role;

    @ApiModelProperty("多靶场类型")
    private Integer multiSceneType;
}
