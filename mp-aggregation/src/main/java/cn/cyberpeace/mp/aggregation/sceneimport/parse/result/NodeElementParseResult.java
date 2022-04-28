package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    private String images;
    private List<NetElementParseResult> net;
    private List<StaticRoutesResult> staticRoutes;
    private List<InitCommandParseResult> initCommands;

    public String getImage() {
        return StringUtils.isNotEmpty(image) ? image : images;
    }
}
