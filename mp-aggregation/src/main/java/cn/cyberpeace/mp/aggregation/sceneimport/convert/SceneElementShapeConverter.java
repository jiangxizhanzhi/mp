package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneAreaBO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 经过多层解析的处理仍没有值的属性, 在这里做默认的处理
 * @Author maojun
 * @Date 2021/10/12
 */
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
            SceneAreaBO areaBO = new SceneAreaBO();
            areaBO.setName(shape.getName());
            String[] xy = shape.getPosition().split(",");
            Map<String, Double> position = new HashMap<>();
            position.put("x", new BigDecimal(xy[0]).setScale(6, RoundingMode.HALF_DOWN).doubleValue());
            position.put("y", new BigDecimal(xy[1]).setScale(6, RoundingMode.HALF_DOWN).doubleValue());
            areaBO.setPosition2(JsonCommon.stringify(position));
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

}
