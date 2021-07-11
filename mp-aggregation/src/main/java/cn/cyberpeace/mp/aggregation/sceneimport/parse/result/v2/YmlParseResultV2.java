package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class YmlParseResultV2 {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResultV2> networks;
    private List<NodeElementParseResult> nodes;
    private List<SceneElementShape> shape;

    public YmlParseResult convert() {
        YmlParseResult result = new YmlParseResult();
        result.setScene(scene);
        result.setNodes(nodes);
        result.setShape(shape);
        result.setNetworks(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(networks)) {
            networks.forEach(n -> {
                result.getNetworks().add(n.convert());
            });
        }
        return result;
    }
}