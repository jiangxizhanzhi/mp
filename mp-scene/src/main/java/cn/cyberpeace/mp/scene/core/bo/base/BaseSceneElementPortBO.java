package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseSceneElementPortBO<M extends BaseSceneElementPortModel> extends EidBO<M> {
    private String id;
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

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        setId(obj.getPortId());
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setPortId(id);
        obj.setId(null);
        return obj;
    }

}
