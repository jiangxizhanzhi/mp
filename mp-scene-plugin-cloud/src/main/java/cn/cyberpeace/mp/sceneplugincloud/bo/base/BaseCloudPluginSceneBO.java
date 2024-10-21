package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCloudPluginSceneBO<M extends BaseCloudPluginSceneModel,
        T extends BaseCloudPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>> extends BasePluginSceneBO<M, T, S> {
    @ApiModelProperty(value = "是否开启录屏")
    private Boolean enableRecording;
    @ApiModelProperty(value = "是否开启环境总速率统计")
    private Boolean enableRate;
    @ApiModelProperty(value = "dnsNameservers")
    private List<DnsPool> dnsNameservers;
    @ApiModelProperty(value = "多云环境下云平台的索引")
    private String cloudIndex;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        //忽略直接赋值处理的字段，需要定制处理
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneModel::getDnsNameservers)
        ));

        dnsNameservers = StringUtils.isEmpty(obj.getDnsNameservers()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getDnsNameservers(), new TypeReference<List<DnsPool>>() {
        });
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneModel::getDnsNameservers)
        ));
        if (dnsNameservers != null) {
            obj.setDnsNameservers(JsonCommon.stringify(dnsNameservers));
        }
        return obj;
    }
}
