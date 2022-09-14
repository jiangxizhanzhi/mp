package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/9/7
 */
@Getter
@Setter
public class Port {
    private String ethertype;
    private String name;
    private Integer portNumber;
    private String type;
    private Integer vlan;
    @JSONField(name = "interface")
    private String Interface;
}
