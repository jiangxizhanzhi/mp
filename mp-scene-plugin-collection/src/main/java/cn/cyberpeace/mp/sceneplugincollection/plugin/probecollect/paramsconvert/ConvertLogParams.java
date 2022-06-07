package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneLogProbeLocationModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 日志采集探针参数转换
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public class ConvertLogParams implements WazuhParamsConvert<WazuhControl.ControlLogCollectParam> {

    private Probe probe;

    private String systemType;

    private Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap;

    private static final String LOG_FORMAT_SYSLOG = "syslog";

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
        if (ProbeConstant.ProbeId.LOG_FILE.equals(probe.getId()) && (probe.getCollectPath() == null || probe.getCollectPath().isEmpty())) {
            return new ArrayList<>();
        }
        int i = 1;
        // 日志文件或日志路径探针单独处理
        if (ProbeConstant.ProbeId.LOG_FILE.equals(probe.getId())) {
            // 每有一个路径添加一个参数
            for (String path : probe.getCollectPath()) {
                WazuhControl.ControlLogCollectParam logParam = new WazuhControl.ControlLogCollectParam();
                logParam.setId(probe.getId() + "_" + i++);
                logParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
                logParam.setLogFormat(LOG_FORMAT_SYSLOG);
                logParam.setLocation(path);
                logParams.add(logParam);
            }
        } else {
            // 探针id和系统类型组成key取日志探针参数
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
