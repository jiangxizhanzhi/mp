package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 注册表变更探针参数转换
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
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
        //每有一个路径添加一个参数
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
