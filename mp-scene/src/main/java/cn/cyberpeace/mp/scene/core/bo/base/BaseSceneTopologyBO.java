package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseSceneTopologyBO<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        P1 extends BaseScenePieceModel,
        C1 extends BaseSceneAreaNodeConnectionModel,
        S extends BaseSceneBO<S0, S>,
        A extends BaseSceneAreaBO<A0, A>,
        P extends BaseSceneElementPortBO<P0, P, E>,
        E extends BaseSceneElementBO<E0, P, E>,
        C extends BaseSceneElementPortConnectionBO<C0, C>,
        C2 extends BaseSceneAreaNodeConnectionBO<C1, C2>,
        P2 extends BaseScenePieceBO<P1, P2>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, P1, C1, S, A, P, E, C, C2, P2, T>> {
    @ApiModelProperty(value = "场景基础数据")
    @Valid
    private S scene;
    @ApiModelProperty(value = "当前区域id")
    private String areaId;
    @ApiModelProperty(value = "区域数据")
    @Valid
    private List<A> areas;
    @ApiModelProperty(value = "网元数据")
    @Valid
    private List<E> elements;
    @ApiModelProperty(value = "连接数据")
    @Valid
    private List<C> connections;
    @ApiModelProperty(value = "节点和区域的连接数据")
    @Valid
    private List<C2> nodeConnections;
    @ApiModelProperty(value = "碎片数据")
    @Valid
    private List<P2> pieces;

    public T export() {
        if (scene != null) {
            setScene(scene.export());
        }
        if (areas != null) {
            List<A> list = new ArrayList<>();
            for (A a : areas) {
                A export = a.export();
                list.add(export);
            }
            setAreas(list);
        }
        if (elements != null) {
            List<E> list = new ArrayList<>();
            for (E e : elements) {
                E export = e.export();
                list.add(export);
            }
            setElements(list);
        }
        if (connections != null) {
            List<C> list = new ArrayList<>();
            for (C c : connections) {
                C export = c.export();
                list.add(export);
            }
            setConnections(list);
        }
        if (pieces != null) {
            List<P2> list = new ArrayList<>();
            for (P2 p2 : pieces) {
                P2 export = p2.export();
                list.add(export);
            }
            setPieces(list);
        }
        if (nodeConnections != null) {
            setNodeConnections(nodeConnections.stream().map(EidBO::export).collect(Collectors.toList()));
        }
        return (T) this;
    }

    public <S9 extends BaseSceneBO<?, S9>,
            A9 extends BaseSceneAreaBO<?, A9>,
            P9 extends BaseSceneElementPortBO<?, P9, E9>,
            E9 extends BaseSceneElementBO<?, P9, E9>,
            C9 extends BaseSceneElementPortConnectionBO<?, C9>,
            C10 extends BaseSceneAreaNodeConnectionBO<?,C10>,
            P7 extends BaseScenePieceBO<?, P7>,
            P8 extends BaseSceneTopologyBO<?, ?, ?,?, ?, ?, ?, S9, A9, P9, E9, C9, C10, P7, P8>> P8 export(
            Class<P8> pClass, Class<S9> sceneClass, Class<A9> areaClass, Class<E9> elementClass, Class<P9> portClass,
            Class<C9> connectionClass,Class<C10> nodeConnectionClass, Class<P7> pieceClass) {
        P8 pObj = ClassUtils.createInstance(pClass);
        pObj.setAreaId(areaId);
        if (scene != null) {
            pObj.setScene(scene.export(sceneClass));
        }
        if (areas != null) {
            List<A9> list = new ArrayList<>();
            for (A area : areas) {
                A9 export = area.export(areaClass);
                list.add(export);
            }
            pObj.setAreas(list);
        }
        if (elements != null) {
            List<E9> list = new ArrayList<>();
            for (E element : elements) {
                E9 export = element.export(elementClass, portClass);
                list.add(export);
            }
            pObj.setElements(list);
        }
        if (connections != null) {
            List<C9> list = new ArrayList<>();
            for (C connection : connections) {
                C9 export = connection.export(connectionClass);
                list.add(export);
            }
            pObj.setConnections(list);
        }
        if (pieces != null) {
            List<P7> list = new ArrayList<>();
            for (P2 piece : pieces) {
                P7 export = piece.export(pieceClass);
                list.add(export);
            }
            pObj.setPieces(list);
        }
        if(nodeConnections != null){
            pObj.setNodeConnections(nodeConnections.stream().map(nodeConnection -> nodeConnection.export(nodeConnectionClass))
                                            .collect(Collectors.toList()));
        }
        return pObj;
    }
}
