package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseSceneAreaBO<M extends BaseSceneAreaModel, T extends BaseSceneAreaBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @Deprecated
    private Boolean open;

    @ApiModelProperty(value = "区域类型")
    private Integer type;


    /**
     * 3d展示场景，分层要求放到elements中
     * @return
     * @param <E>
     */
    public abstract <E extends BaseSceneElementBO<?, ?, ?>> E convertSceneAreaToElement(List<BaseSceneAreaNodeConnectionBO> areaNodeConnectionBOS);

}
