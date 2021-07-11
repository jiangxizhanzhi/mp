package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class SceneElementShapeConverter implements IConverter {

    private List<SceneElementShape> shapes;

    public SceneElementShapeConverter(List<SceneElementShape> shapes) {
        this.shapes = shapes;
    }

    @Override
    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isEmpty(this.shapes)) {
            return;
        }
        Map<String, String> nodeId2AreaId = new HashMap<>();
        for (var shape : shapes) {
            SceneConfigAreaBO areaBO = new SceneConfigAreaBO();
            areaBO.setId(shape.getId());
            areaBO.setName(shape.getName());
            areaBO.setDisplayContext(shape.getDisplayContext());
            double width = new BigDecimal(shape.getWidth()).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
            double height = new BigDecimal(shape.getLength()).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
            Position position = new Position();
            position.setHeight(height);
            position.setWidth(width);
            String[] xy = shape.getPosition().split(",");
            position.setOrigin(new Origin(formatDouble(xy[0]), formatDouble(xy[1])));
            areaBO.setPosition2(JsonCommon.stringify(position));
            container.addAreaElement(areaBO);
            if (CollectionUtils.isNotEmpty(shape.getNodeIds())) {
                for (String nodeId : shape.getNodeIds()) {
                    nodeId2AreaId.put(nodeId, shape.getId());
                }
            }
        }
        for (var element : container.getAllElements()) {
            if (nodeId2AreaId.containsKey(element.getId())) {
                element.setArea(nodeId2AreaId.get(element.getId()));
            }
        }
    }

    private double formatDouble(double number) {
        return BigDecimal.valueOf(number).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
    }

    private double formatDouble(String number) {
        return new BigDecimal(number).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
    }

    @Getter
    @Setter
    private static class Position {
        private Origin origin;
        private double height;
        private double width;
    }

    @Getter
    @Setter
    private static class Origin {
        private double x;
        private double y;

        public Origin(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
