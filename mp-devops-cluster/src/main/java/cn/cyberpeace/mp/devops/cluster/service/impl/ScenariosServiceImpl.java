package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ScenarioDeleteDeviceParams;
import cn.cyberpeace.mp.devops.cluster.model.ScenariosSnapshot;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.service.ScenariosSnapshotService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ScenariosServiceImpl extends BaseOpenStackServiceImpl implements ScenariosSnapshotService {

    public ScenariosServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public Snapshot create(ScenariosSnapshot snapshotCreate) {
        return super.sendPost(  URL_VERSION + "/scenarios/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    @Override
    public Boolean deleteDeviceScenario(ScenarioDeleteDeviceParams params) {
        BaseCloudPlatformModel model = super.sendDelete( URL_VERSION + "/scenarios/device_scenario",JsonUtils.toUnderlineJSONString(params), BaseCloudPlatformModel.class);
        return model.isSuccess();
    }
}
