package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneLogProbeLocationModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertLogParams implements WazuhParamsConvert<WazuhControl.ControlLogCollectParam> {

    private Probe probe;

    private String systemType;

    private Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap;

    private static final String LOG_FORMAT_SYSLOG = "syslog";

    private static final String LOG_ID = "log";
    private static final String SECURITY_ID = "security";
    private static final String OS_ID = "os";
    private static final String APPLICATION_ID = "application";

    public ConvertLogParams(Probe probe, String systemType, Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap) {
        this.probe = probe;
        this.systemType = SystemTypeCommandUtils.getOsType(systemType);
        this.locationMap = locationMap;
    }

    @Override
    public List<WazuhControl.ControlLogCollectParam> convertParams() {
        List<WazuhControl.ControlLogCollectParam> logParams = new ArrayList<>();
        if (probe.getDisabled()) {
            return new ArrayList<>();
        }
        if (LOG_ID.equals(probe.getId()) && (probe.getCollectPath() == null || probe.getCollectPath().isEmpty())) {
            return new ArrayList<>();
        }
        int i = 1;
        if (LOG_ID.equals(probe.getId())) {
            for (String path : probe.getCollectPath()) {
                WazuhControl.ControlLogCollectParam logParam = new WazuhControl.ControlLogCollectParam();
                logParam.setId(probe.getId() + "_" + i++);
                logParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
                logParam.setLogFormat(LOG_FORMAT_SYSLOG);
                logParam.setLocation(path);
                logParams.add(logParam);
            }
        } else {
            String key = probe.getId() + "_" + systemType;
            List<CollectionPluginSceneLogProbeLocationModel> locationModels = locationMap.get(key);
            if (locationModels == null || locationModels.isEmpty()) {
                return new ArrayList<>();
            }
            for (CollectionPluginSceneLogProbeLocationModel locationModel : locationModels) {
                WazuhControl.ControlLogCollectParam logParam = new WazuhControl.ControlLogCollectParam();
                logParam.setId(probe.getId() + "_" + i++);
                logParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
                logParam.setLogFormat(locationModel.getLogFormat());
                logParam.setLocation(locationModel.getLocation());
                logParams.add(logParam);
            }
        }
        return logParams;
    }

}
