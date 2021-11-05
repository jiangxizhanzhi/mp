package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.model.base.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseSceneTopologyBO<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        S extends BaseSceneBO<S0, S>,
        A extends BaseSceneAreaBO<A0, A>,
        P extends BaseSceneElementPortBO<P0, P>,
        E extends BaseSceneElementBO<E0, P, E>,
        C extends BaseSceneElementPortConnectionBO<C0, C>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, S, A, P, E, C, T>> {
    @ApiModelProperty(value = "场景基础数据")
    private S scene;
    @ApiModelProperty(value = "当前区域id")
    private String areaId;
    @ApiModelProperty(value = "区域数据")
    private List<A> areas;
    @ApiModelProperty(value = "网元数据")
    private List<E> elements;
    @ApiModelProperty(value = "连接数据")
    private List<C> connections;

    public T export() {
        if (scene != null) {
            setScene(scene.export());
        }
        if (areas != null) {
            setAreas(areas.stream().map(EidBO::export).collect(Collectors.toList()));
        }
        if (elements != null) {
            setElements(elements.stream().map(BaseSceneElementBO::export).collect(Collectors.toList()));
        }
        if (connections != null) {
            setConnections(connections.stream().map(EidBO::export).collect(Collectors.toList()));
        }
        return (T) this;
    }

    public <S9 extends BaseSceneBO<?, S9>,
            A9 extends BaseSceneAreaBO<?, A9>,
            P9 extends BaseSceneElementPortBO<?, P9>,
            E9 extends BaseSceneElementBO<?, P9, E9>,
            C9 extends BaseSceneElementPortConnectionBO<?, C9>,
            P8 extends BaseSceneTopologyBO<?, ?, ?, ?, ?, S9, A9, P9, E9, C9, P8>> P8 export(
            Class<P8> pClass, Class<S9> sceneClass, Class<A9> areaClass, Class<E9> elementClass, Class<P9> portClass,
            Class<C9> connectionClass) {
        var pObj = ClassUtils.createInstance(pClass);
        pObj.setAreaId(areaId);
        if (scene != null) {
            pObj.setScene(scene.export(sceneClass));
        }
        if (areas != null) {
            pObj.setAreas(areas.stream().map(area -> area.export(areaClass)).collect(Collectors.toList()));
        }
        if (elements != null) {
            pObj.setElements(elements.stream().map(element -> element.export(elementClass, portClass))
                    .collect(Collectors.toList()));
        }
        if (connections != null) {
            pObj.setConnections(connections.stream().map(connection -> connection.export(connectionClass))
                    .collect(Collectors.toList()));
        }
        return pObj;
    }
}
