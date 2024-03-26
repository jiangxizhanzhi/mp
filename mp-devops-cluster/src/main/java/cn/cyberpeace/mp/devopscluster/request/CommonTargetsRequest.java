package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.request.queryparam.MultiSceneCommonTargetsQueryParams;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommonTargetsRequest {
    /**
     * 镜像类型
     */
    @ApiModelProperty("镜像类型")
    private ImageTypeEnum imageType;

    @ApiModelProperty("角色")
    private Integer role;

    @ApiModelProperty("多靶场类型")
    private Integer multiSceneType = BaseSceneModel.MultiSceneType.COMMON_SCENE;

    @ApiModelProperty("靶场标识")
    private String rangeTag;

    public MultiSceneCommonTargetsQueryParams multiSceneCommonTargetsQueryParams() {
        MultiSceneCommonTargetsQueryParams multiSceneCommonTargetsQueryParams = new MultiSceneCommonTargetsQueryParams();
        multiSceneCommonTargetsQueryParams.setMultiSceneType(BaseSceneModel.MultiSceneType.COMMON_SCENE);
        multiSceneCommonTargetsQueryParams.setRole(this.getRole());
        multiSceneCommonTargetsQueryParams.setImageType(this.getImageType());
        return multiSceneCommonTargetsQueryParams;
    }
}
