package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "场景实例", tags = "CSCENE-场景实例")
@Slf4j
@RestController
@RequestMapping("/unauth/cscene_instances")
@Data
public class CsceneUnauthController implements BaseModelListControllerBase {
    /**
     * 查询节点所在网络对应的采集路由ip
     * @param uuid
     * @return
     */
    @GetMapping("/get_route_ip")
    @ApiOperation(value = "查询节点所在网络对应的采集路由ip")
    public String getCollectRouteIp(String uuid) {
        CloudPluginSceneElementMapper mapper = BeanCommon.getBean(CloudPluginSceneElementMapper.class);
        var cond = new HumanCondition().link(SceneModel.class, CloudPluginSceneElementModel.class)
                .filterLink(SceneModel.class, SceneModel::getId, CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(SceneModel.class).filterEqual(SceneModel::getDeleted, false)
                .currentLink(CloudPluginSceneElementModel.class)
                .filterEqual(CloudPluginSceneElementModel::getImageUuid, uuid);
        var list = mapper.list(cond, CloudPluginSceneElementModel.class);
        if (list == null || list.size() == 0) {
            return "该靶标未应用在场景中!";
        }
        List<String> sceneIds = new ArrayList<>();
        list.forEach(l -> sceneIds.add(l.getScene()));
        if (sceneIds.size() > 1) {
            log.info("{} 靶标应用在了多个场景中 {} ", uuid, JsonCommon.stringify(sceneIds));
        }
        String sceneId = sceneIds.get(0);
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CollectionScenePluginHandler handler = sceneHandler.getPluginHandler("cpcollection");
        String name = sceneHandler.getScene().getName();
        if (handler == null || StringUtils.isEmpty(handler.getScene().getProbeRouter())) {
            return "靶标应用在场景`" + name + "`中，但是未开启数据采集";
        }
        String routerId = handler.getScene().getProbeRouter();
        log.info("采集路由id: {}", routerId);

        var nets = new VirtualRouter().get(routerId);
        log.info("路由的网络信息:{}", JsonCommon.stringify(nets));
        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getPluginHandler("cpcloud");

        CloudPluginSceneElementBO cloudElement = null;
        for (var e : cloudScenePluginHandler.getElementMap().values()) {
            if (StringUtils.equals(e.getImageUuid(), uuid)) {
                cloudElement = e;
                break;
            }
        }
        if (cloudElement == null) {
            return "在场景`" + name + "`中未发现靶标";
        }
        var srcElement = cloudElement.getSrcElement();
        if (srcElement.getPorts() == null || srcElement.getPorts().size() == 0) {
            return "靶标应用在场景`" + name + "`中，但是未配置端口";
        }
        for (var port : srcElement.getPorts()) {
            if (sceneHandler.getPortConnectedPort(port.getId()) == null) {
                continue;
            }
            if (StringUtils.isEmpty(port.getIp())) {
                continue;
            }
            for (var net : nets) {
                String cidr = IpUtils.getFormatCidrIp(net.getFixedIp() + "/24");
                if (IpUtils.newIsInCidr2(port.getIp(), cidr)) {
                    return "节点采集路由网关为: " + net.getFixedIp();
                }
            }
        }

        return "未发现和靶标同网段的采集路由网关";
    }
}
