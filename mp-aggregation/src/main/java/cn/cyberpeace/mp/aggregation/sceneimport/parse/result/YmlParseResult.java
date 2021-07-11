package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class YmlParseResult {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResult> networks;
    private List<NodeElementParseResult> nodes;
    private List<SceneElementShape> shape;
}