package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

@Data
public class QuickCreatePageParam implements SnakeHttpParam {
    private String imageType;
    private String language;
    private String systemArchitecture;
    private String systemType;
    private String flavor;
    private String name;
    private Integer limit;
    private Integer offset;
}
