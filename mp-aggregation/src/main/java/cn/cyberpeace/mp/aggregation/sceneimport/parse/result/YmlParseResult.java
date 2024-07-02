package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName YmlParseResult
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 * @Version 1.0.0
 */
@Getter
@Setter
public class YmlParseResult {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResult> networks;
    private List<NodeElementParseResult> nodes;
    private List<SceneElementShape> shape;
}