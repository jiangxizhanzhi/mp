package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    private List<AccessMode> accessModes;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields, "accessModes"));
        setId(obj.getPortId());
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAccessModes(), new TypeReference<List<AccessMode>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields, "accessModes"));
        obj.setPortId(id);
        if (accessModes != null) {
            obj.setAccessModes(JsonUtils.toJsonString(accessModes));
        }
        return obj;
    }

}
