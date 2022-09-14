package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param;

import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateProjectParams implements Gns3HttpData {
    private String name;
}
