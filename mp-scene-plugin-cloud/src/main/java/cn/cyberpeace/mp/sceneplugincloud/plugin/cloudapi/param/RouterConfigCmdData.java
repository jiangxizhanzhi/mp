package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/19
 */
@Getter
@Setter
public class RouterConfigCmdData implements CloudHttpData {
    private String serialIp;
    private Integer serialPort;
    private List<String> cmds;
    private String cmd;
}
