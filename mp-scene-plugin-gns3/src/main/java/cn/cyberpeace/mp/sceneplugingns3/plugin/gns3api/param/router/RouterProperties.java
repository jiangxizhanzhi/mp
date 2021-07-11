package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseProperties;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RouterProperties extends BaseProperties {
    private String image;
    private String platform;
    private Integer ram;
    private Integer dynamipsId;
}
