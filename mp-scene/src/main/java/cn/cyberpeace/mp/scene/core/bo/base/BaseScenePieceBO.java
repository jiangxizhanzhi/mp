package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseScenePieceBO<M extends BaseScenePieceModel, T extends BaseScenePieceBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @ApiModelProperty(value = "碎片类型")
    private String type;
}
