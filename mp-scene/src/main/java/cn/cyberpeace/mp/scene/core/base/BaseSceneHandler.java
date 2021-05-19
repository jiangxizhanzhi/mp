package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.scene.model.base.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class BaseSceneHandler<S extends BaseSceneModel, A extends BaseSceneAreaModel, E extends BaseSceneElementModel,
        P extends BaseSceneElementPortModel, C extends BaseSceneElementPortConnectionModel> {

    @Getter
    private S scene;
    @Getter
    private String areaId;
    @Getter
    private List<A> areas;
    @Getter
    private List<E> elements;
    @Getter
    @Setter
    private List<C> connections;

    @Getter
    private Map<String, A> areaMap;
    @Getter
    private Map<String, E> elementMap;
    @Getter
    private Map<String, P> portMap;

    public void addArea(A area) {
        areas.add(area);
        areaMap.put(area.getNodeId(), area);
    }

    public void addElement(E element) {
        elements.add(element);
        elementMap.put(element.getNodeId(), element);
    }

    public void addConnection(C connection) {
        connections.add(connection);
    }

    public void setAreas(List<A> areas) {
        this.areas = areas;
        areas.forEach(this::addArea);
    }

    public void setElements(List<E> elements) {
        this.elements = elements;
        elements.forEach(this::addElement);
    }

}
