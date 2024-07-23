 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementBO.java.
 // File size 3093.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
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

/**
 * @ClassName CloudPluginSceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementBO
 */
@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel,
        CloudPluginSceneElementBO, SceneElementBO> {

    @ApiModelProperty(value = "靶标在配置网络上分到的ip")
    private String configIp;

    @JsonIgnore
    private CoreType coreType;

    @ApiModelProperty(value = "靶标快照数量,仅消息推送中有该字段")
    private Integer imageSnapshotNum;

    @ApiModelProperty(value = "云平台用于分布式部署时需要的ip,节点的每个网络需要一个")
    private Map<String, String> fwdnsIps = new ConcurrentHashMap<>();

    @ApiModelProperty(value = "是否支持查询软件版本")
    private Boolean supportSoftVersion;

    // 加入的网络
    @JsonIgnore
    public List<ConnectedNetworkBO> accessNetworks = new ArrayList<>();

    // 构建的网络
    @JsonIgnore
    private List<ConnectedNetworkBO> deriveNetworks = new ArrayList<>();

    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }

    /**
     * fromModel
     *
     * @param obj
     * @param ignoreFields
     */
    @Override
    public void fromModel(CloudPluginSceneElementModel obj, String... ignoreFields) {
        super.fromModel(obj, "fwdnsIps");
        fwdnsIps = StringUtils.isEmpty(obj.getFwdnsIps()) ? new HashMap<>()
                : JsonCommon.parse(obj.getFwdnsIps(), new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * toModel
     *
     * @param ignoreFields
     * @return
     */
    public CloudPluginSceneElementModel toModel(String... ignoreFields) {
        CloudPluginSceneElementModel obj = super.toModel("fwdnsIps");
        if (fwdnsIps != null) {
            obj.setFwdnsIps(JsonCommon.stringify(fwdnsIps));
        }
        return obj;
    }

    public Boolean getSupportSoftVersion() {
        return StringUtils.equals(SceneSingleton.sceneAppConfig.getVersionImage(), this.getImage());
    }
}
