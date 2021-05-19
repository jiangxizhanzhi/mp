package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.*;
import lombok.Data;

import java.util.List;

@Data
public class BaseSceneTopologyBO<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        S extends BaseSceneBO<S0>,
        A extends BaseSceneAreaBO<A0>,
        P extends BaseSceneElementPortBO<P0>,
        E extends BaseSceneElementBO<E0, P>,
        C extends BaseSceneElementPortConnectionBO<C0>> {
    private S scene;
    private String areaId;
    private List<A> areas;
    private List<E> elements;
    private List<C> connections;
}
