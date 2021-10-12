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
public class NodeElementParseResult {
    private String id;
    private String name;
    private String image;
    private List<NetElementParseResult> net;
    private List<InitCommandParseResult> initCommands;
}
