package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseSceneElementPortModel extends BaseModel {
    private String scene;
    private String sceneElement;
    @DefaultGen(UuidGenerator.class)
    private String portId;
    @DefaultStr
    private String name;
    @DefaultInt(Type.IP_PORT)
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
    @DefaultStr
    private String accessModes;
    @DefaultInt
    private Integer order;

    public static class Type {
        public static final int IP_PORT = 1;
        public static final int MAC_PORT = 2;
        public static final int SERIAL_PORT = 3;

        private Type() {}
    }
}
