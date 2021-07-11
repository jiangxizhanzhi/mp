package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class NodeElementParseResult {
    private String id;
    private String name;
    private String position;
    private String image;
    private String images;
    private String file;
    private String virtualPlatform;
    private List<NetElementParseResult> net;
    private List<StaticRoutesResult> staticRoutes;
    private List<StaticRoutesResult> staticRouters;
    private List<InitCommandParseResult> initCommands;
    private ExtNodeInfo extInfo;
    private String displayContext;

    public String getImage() {
        return StringUtils.isNotEmpty(image) ? image : images;
    }

    public List<StaticRoutesResult> getUsedStaticRoutes() {
        if (CollectionUtils.isNotEmpty(staticRoutes)) {
            return staticRoutes;
        } else if (CollectionUtils.isNotEmpty(staticRouters)) {
            return staticRouters;
        } else {
            return new ArrayList<>();
        }
    }
}
