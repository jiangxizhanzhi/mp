package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.model.base.*;
import lombok.Getter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseSceneHandler<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        S extends BaseSceneBO<S0>,
        A extends BaseSceneAreaBO<A0>,
        P extends BaseSceneElementPortBO<P0>,
        E extends BaseSceneElementBO<E0, P>,
        C extends BaseSceneElementPortConnectionBO<C0>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, S, A, P, E, C>,
        D extends BaseSceneDataBO<T>,
        M1 extends BaseModelMapper<S0>,
        M2 extends BaseModelMapper<A0>,
        M3 extends BaseModelMapper<E0>,
        M4 extends BaseModelMapper<P0>,
        M5 extends BaseModelMapper<C0>> {

    @Getter
    private S scene;
    @Getter
    private String areaId;
    @Getter
    private final Map<String, A> areaMap = new HashMap<>();
    @Getter
    private final Map<String, E> elementMap = new HashMap<>();
    @Getter
    private final Map<String[], C> connectionMap = new HashMap<>();

    @Resource
    private M1 sceneMapper;
    @Resource
    private M2 sceneAreaMapper;
    @Resource
    private M3 sceneElementMapper;
    @Resource
    private M4 sceneElementPortMapper;
    @Resource
    private M5 sceneElementPortConnectionMapper;

    public void initFromSceneDataBO(D sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        scene = topology.getScene();
        areaId = topology.getAreaId();
        List<A> areas = topology.getAreas();
        if (areas != null) {
            areas.forEach(area -> areaMap.put(area.getId(), area));
        }
        List<E> elements = topology.getElements();
        if (elements != null) {
            elements.forEach(element -> elementMap.put(element.getId(), element));
        }
        List<C> connections = topology.getConnections();
        if (connections != null) {
            connections.forEach(connection -> connectionMap.put(
                    new String[]{connection.getSource(), connection.getTarget()}, connection));
        }

    }

    public void save() {

    }

}
