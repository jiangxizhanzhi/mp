package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel,
        CloudPluginSceneElementBO, SceneElementBO> {

    @JsonIgnore
    private CoreType coreType;

    @ApiModelProperty(value = "靶标快照数量,仅消息推送中有该字段")
    private Integer imageSnapshotNum;

    @ApiModelProperty(value = "云平台用于分布式部署时需要的ip,节点的每个网络需要一个")
    private Map<String, String> fwdnsIps = new ConcurrentHashMap<>();

    
    @JsonIgnore
    public List<ConnectedNetworkBO> accessNetworks = new ArrayList<>();

    
    @JsonIgnore
    private List<ConnectedNetworkBO> deriveNetworks = new ArrayList<>();

    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }

    @Override
    public void fromModel(CloudPluginSceneElementModel obj, String... ignoreFields) {
        super.fromModel(obj, "fwdnsIps");
        fwdnsIps = StringUtils.isEmpty(obj.getFwdnsIps()) ? new HashMap<>()
                : JsonCommon.parse(obj.getFwdnsIps(), new TypeReference<Map<String, String>>() {
        });
    }

    public CloudPluginSceneElementModel toModel(String... ignoreFields) {
        var obj = super.toModel("fwdnsIps");
        if (fwdnsIps != null) {
            obj.setFwdnsIps(JsonCommon.stringify(fwdnsIps));
        }
        return obj;
    }

}
