package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseSceneAreaBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeBO
 */
@Data
public abstract class BaseSceneAreaBO<M extends BaseSceneAreaModel, T extends BaseSceneAreaBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @Deprecated
    private Boolean open;

    @ApiModelProperty(value = "区域类型")
    private Integer type;

}
