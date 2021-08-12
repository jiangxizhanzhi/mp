package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneElementPortModel extends BaseModel {
    private String scene;
    private String sceneElement;
    private String portId;
    @DefaultStr
    private String name;
    @DefaultInt(1)
    private Integer type;
    @DefaultStr
    private String macAddr;
    @DefaultStr
    private String ip;
    @DefaultStr
    private String netmask;
    @DefaultStr
    private String gateway;
    @DefaultBool
    private Boolean dhcp;
    @DefaultStr
    private String dns;
    @DefaultInt(-1)
    private Integer egress;
    @DefaultInt(-1)
    private Integer ingress;
    @DefaultInt
    private Integer delay;
    @DefaultInt
    private Integer loss;

    public static class Type {
        public static final Integer IP_PORT = 1;
        public static final Integer MAC_PORT = 2;
        public static final Integer SERIAL_PORT = 3;

        private Type() {}
    }
}
