package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneContextBO {

    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;

    @ApiModelProperty(value = "透明上下文")
    private Map<String, Object> passThrough;

    public void loadTransparentContext() {
        if (passThrough != null) {
            SysContextHolder.setPassThroughMap(passThrough);
        }
    }

    public SceneContextBO toSceneContext() {
        var sceneContext = new SceneContextBO();
        Map<String, Object> plugins = new HashMap<>();
        var cloudScenePluginContext = new CloudScenePluginContext();
        if (StringUtils.isEmpty(accessUserId)) {
            cloudScenePluginContext.setRequestUser(accessUserId);
            var user = UserCommon.getUserDetail(accessUserId);
            if (user != null) {
                var orgs = user.getOrgs();
                if (!CollectionUtils.isEmpty(orgs)) {
                    var orgName = orgs.get(0).getName();
                    cloudScenePluginContext.setRequestUserOrg(orgName);
                }
            }
        }
        plugins.put(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY, cloudScenePluginContext);
        sceneContext.setPlugins(plugins);
        return sceneContext;
    }
}
