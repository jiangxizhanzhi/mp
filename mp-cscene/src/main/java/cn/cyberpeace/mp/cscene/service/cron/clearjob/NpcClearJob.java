package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Npc;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.BaseNpcData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcGetData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NpcClearJob extends AbstractClearJob<NpcGetData> {

    private SceneElementPortMapper elementPortMapper;

    private CloudPluginSceneElementPortMapper cloudPortMapper;

    private Npc npc = new Npc();

    public NpcClearJob(SceneElementPortMapper elementPortMapper, CloudPluginSceneElementPortMapper cloudPortMapper, SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
        this.cloudPortMapper = cloudPortMapper;
        this.elementPortMapper = elementPortMapper;
    }

    @Override
    protected Map<String, NpcGetData> getEid2Node(List<NpcGetData> npcs) {
        Map<String, NpcGetData> result = new HashMap<>();
        npcs.forEach(npc -> {
            if (npc.getNodeName() != null) {
                String eid = super.getEid(npc.getNodeName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, npc);
                }
            }
        });
        return result;
    }

    @Override
    protected List<NpcGetData> doQuery(int offset) {
        return npc.getAllNpc(LIMIT, offset);
    }

    @Override
    protected void doClear(NpcGetData data) {
        String eid = super.getEid(data.getNodeName());
        List<SceneElementModel> elements = sceneElementMapper.list(
                new HumanCondition().filterEqual(SceneElementModel::getId, eid),
                SceneElementModel.class
        );
        if (CollectionUtils.isEmpty(elements) || elements.size() > 1) {
            return;
        }
        SceneElementModel element = elements.get(0);

        List<SceneElementPortModel> ports = elementPortMapper.list(
                new HumanCondition().filterEqual(SceneElementPortModel::getScene, element.getScene())
                        .filterEqual(SceneElementPortModel::getSceneElement, element.getId()),
                SceneElementPortModel.class
        );
        if (CollectionUtils.isEmpty(ports) || ports.size() > 1) {
            return;
        }
        SceneElementPortModel port = ports.get(0);
        if (!StringUtils.equals(data.getNodeIp(), port.getIp())) {
            return;
        }

        List<CloudPluginSceneElementPortModel> cloudPorts = cloudPortMapper.list(
                new HumanCondition().filterEqual(CloudPluginSceneElementPortModel::getScene, port.getScene())
                        .filterEqual(CloudPluginSceneElementPortModel::getSceneElementPort, port.getPortId()),
                CloudPluginSceneElementPortModel.class
        );
        if (CollectionUtils.isEmpty(cloudPorts) || cloudPorts.size() > 1) {
            return;
        }
        CloudPluginSceneElementPortModel cloudPort = cloudPorts.get(0);
        String networkId = cloudPort.getAuxiliaryNetworkId();
        if (StringUtils.isEmpty(networkId)) {
            return;
        }
        NpcDeleteData npcDeleteData = new NpcDeleteData();
        BaseNpcData.NetWorkNpcInfo netWorkNpcInfo = new BaseNpcData.NetWorkNpcInfo(networkId);
        netWorkNpcInfo.addNpcInfo(data.getNodeName(), data.getNodeIp());
        npcDeleteData.setNetWorkNpcInfos(Arrays.asList(netWorkNpcInfo));
        npc.delete(npcDeleteData);
    }
}
