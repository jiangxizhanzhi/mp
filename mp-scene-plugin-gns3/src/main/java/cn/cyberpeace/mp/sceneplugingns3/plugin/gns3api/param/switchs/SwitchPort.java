package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.switchs;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/9/8
 */
@Getter
@Setter
public class SwitchPort {
    private String type;
    private Integer portNumber;
    private String name;
    private Integer vlan;
}
