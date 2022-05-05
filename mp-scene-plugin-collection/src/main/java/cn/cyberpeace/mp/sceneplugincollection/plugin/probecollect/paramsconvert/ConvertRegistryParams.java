package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public class ConvertRegistryParams implements WazuhParamsConvert<WazuhControl.ControlRegistryModifyParam> {

    private Probe probe;

    public ConvertRegistryParams(Probe probe) {
        this.probe = probe;
    }

    @Override
    public List<WazuhControl.ControlRegistryModifyParam> convertParams() {
        if (probe.getDisabled() || probe.getCollectPath() == null || probe.getCollectPath().isEmpty()) {
            return new ArrayList<>();
        }
        List<WazuhControl.ControlRegistryModifyParam> registryParams = new ArrayList<>();
        int i = 1;
        for (String path : probe.getCollectPath()) {
            WazuhControl.ControlRegistryModifyParam registryParam = new WazuhControl.ControlRegistryModifyParam();
            registryParam.setId(probe.getId() + "_" + i++);
            registryParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
            registryParam.setTargets(CollectionUtil.toList(path));
            registryParams.add(registryParam);
        }
        return registryParams;
    }
}
