package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseScenePieceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeBO
 */
@Data
public abstract class BaseScenePieceBO<M extends BaseScenePieceModel, T extends BaseScenePieceBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @ApiModelProperty(value = "碎片类型")
    private String type;
}
