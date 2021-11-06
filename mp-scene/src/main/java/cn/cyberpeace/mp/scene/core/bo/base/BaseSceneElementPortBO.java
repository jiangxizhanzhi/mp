package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseSceneElementPortBO<M extends BaseSceneElementPortModel,
        T extends BaseSceneElementPortBO<M, T>> extends EidBO<M, T> {
    @ApiModelProperty(value = "端口id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "类型")
    private Integer type;
    @ApiModelProperty(value = "mac地址")
    private String macAddr;
    @ApiModelProperty(value = "ip地址")
    private String ip;
    @ApiModelProperty(value = "子网掩码")
    private String netmask;
    @ApiModelProperty(value = "网关")
    private String gateway;
    @ApiModelProperty(value = "是否开启dhcp")
    private Boolean dhcp;
    @ApiModelProperty(value = "dns")
    private String dns;
    @ApiModelProperty(value = "上行带宽")
    private Integer egress;
    @ApiModelProperty(value = "下行带宽")
    private Integer ingress;
    @ApiModelProperty(value = "延迟")
    private Integer delay;
    @ApiModelProperty(value = "丢包率")
    private Integer loss;
    @ApiModelProperty(value = "接入方式")
    private List<AccessMode> accessModes;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementPortModel::getAccessModes)));
        setId(obj.getPortId());
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAccessModes(), new TypeReference<List<AccessMode>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementPortModel::getAccessModes)));
        obj.setPortId(id);
        if (accessModes != null) {
            obj.setAccessModes(JsonCommon.stringify(accessModes));
        }
        return obj;
    }

}
