package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseSceneAreaBO<M extends BaseSceneAreaModel, T extends BaseSceneAreaBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @ApiModelProperty(value = "展开标记")
    private Boolean open;
}
