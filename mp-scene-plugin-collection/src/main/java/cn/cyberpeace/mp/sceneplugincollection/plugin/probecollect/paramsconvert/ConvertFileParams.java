package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;


public class ConvertFileParams implements WazuhParamsConvert<WazuhControl.ControlFileModifyParam> {

    private Probe probe;

    private Integer getFileRecursionLevel() {
        return CollectionSingleton.collectionAppConfig.getFileRecursionLevel();
    }

    public ConvertFileParams(Probe probe) {
        this.probe = probe;
    }

    @Override
    public List<WazuhControl.ControlFileModifyParam> convertParams() {
        List<WazuhControl.ControlFileModifyParam> fileParams = new ArrayList<>();
        if (probe.getDisabled() || probe.getCollectPath() == null || probe.getCollectPath().isEmpty()) {
            return new ArrayList<>();
        }
        int i = 1;
        
        for (String path : probe.getCollectPath()) {
            WazuhControl.ControlFileModifyParam fileParam = new WazuhControl.ControlFileModifyParam();
            fileParam.setId(probe.getId() + "_" + i++);
            fileParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
            fileParam.setRealtimeTargets(CollUtil.toList(path));
            fileParam.setRecursionLevel(getFileRecursionLevel());
            fileParams.add(fileParam);
        }
        return fileParams;
    }
}
