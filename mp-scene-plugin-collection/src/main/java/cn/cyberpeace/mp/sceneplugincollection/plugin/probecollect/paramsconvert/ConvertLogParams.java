package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhLogProbeLocationConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConvertLogParams implements WazuhParamsConvert<WazuhControl.ControlLogCollectParam> {

    private Probe probe;

    private String systemType;

    private Map<String, List<WazuhLogProbeLocationConfig.ProbeLocation>> locationMap;

    private static final String LOG_FORMAT_SYSLOG = "syslog";

    public ConvertLogParams(Probe probe, String systemType, Map<String, List<WazuhLogProbeLocationConfig.ProbeLocation>> locationMap) {
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
        if (ProbeConstant.ProbeId.LOG_FILE.equals(probe.getId()) && (probe.getCollectPath() == null || probe.getCollectPath().isEmpty())) {
            return new ArrayList<>();
        }
        int i = 1;
        
        if (ProbeConstant.ProbeId.LOG_FILE.equals(probe.getId())) {
            
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
            List<WazuhLogProbeLocationConfig.ProbeLocation> locations = locationMap.get(key);
            if (locations == null || locations.isEmpty()) {
                return new ArrayList<>();
            }
            for (WazuhLogProbeLocationConfig.ProbeLocation location : locations) {
                for (String l : location.getLocation()) {
                    WazuhControl.ControlLogCollectParam logParam = new WazuhControl.ControlLogCollectParam();
                    logParam.setId(probe.getId() + "_" + i++);
                    logParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
                    logParam.setLogFormat(location.getLogFormat());
                    logParam.setLocation(l);
                    logParams.add(logParam);
                }
            }
        }
        return logParams;
    }

}
