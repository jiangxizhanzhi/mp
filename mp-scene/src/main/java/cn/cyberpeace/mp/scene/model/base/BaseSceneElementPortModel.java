package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public class BaseSceneElementPortModel extends BaseModel {
    private String sceneElement;
    private String portId;
    private String name;
    private Integer type;
    private String macAddr;
    private String ip;
    private String netmask;
    private String gateway;
    private Boolean dhcp;
    private String dns;
    private Integer egress;
    private Integer ingress;
    private Integer delay;
    private Integer loss;
}
