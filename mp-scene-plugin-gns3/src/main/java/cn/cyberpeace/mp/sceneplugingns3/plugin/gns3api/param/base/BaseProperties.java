package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/9/7
 */
@Getter
@Setter
public class BaseProperties {
    private List<Port> portsMapping;

    public void addPort(Port port) {
        if (portsMapping == null) {
            this.portsMapping = new ArrayList<>();
        }
        this.portsMapping.add(port);
    }
}
