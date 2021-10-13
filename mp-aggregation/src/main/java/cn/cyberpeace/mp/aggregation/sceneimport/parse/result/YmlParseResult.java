package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 */
@Getter
@Setter
public class YmlParseResult {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResult> networks;
    private List<NodeElementParseResult> nodes;
}