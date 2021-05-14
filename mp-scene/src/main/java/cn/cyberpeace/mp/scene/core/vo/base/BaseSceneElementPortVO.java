package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseSceneElementPortVO<M extends BaseSceneElementPortModel> extends EidVO<M> {
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
    public void fromModel(M obj) {
        super.fromModel(obj);
        setId(obj.getPortId());
    }

    @Override
    public M toModel() {
        var obj = super.toModel();
        obj.setPortId(id);
        obj.setId(null);
        return obj;
    }

}
