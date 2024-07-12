 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConvertRegistryParams.java.
 // File size 2105.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.base.util.mptool.CollUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ConvertRegistryParams
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see WazuhParamsConvert
 */
public class ConvertRegistryParams implements WazuhParamsConvert<WazuhControl.ControlRegistryModifyParam> {

    private Probe probe;

    /**
     * getRegistryRecursionLevel
     *
     * @return
     */
    private Integer getRegistryRecursionLevel() {
        return CollectionSingleton.collectionAppConfig.getRegistryRecursionLevel();
    }

    /**
     * ConvertRegistryParams
     *
     * @param probe
     */
    public ConvertRegistryParams(Probe probe) {
        this.probe = probe;
    }

    /**
     * convertParams
     *
     * @return
     */
    @Override
    public List<WazuhControl.ControlRegistryModifyParam> convertParams() {
        if (probe.getDisabled() || probe.getCollectPath() == null || probe.getCollectPath().isEmpty()) {
            return new ArrayList<>();
        }
        List<WazuhControl.ControlRegistryModifyParam> registryParams = new ArrayList<>();
        int i = 1;
        // 每有一个路径添加一个参数
        for (String path : probe.getCollectPath()) {
            WazuhControl.ControlRegistryModifyParam registryParam = new WazuhControl.ControlRegistryModifyParam();
            registryParam.setId(probe.getId() + "_" + i++);
            registryParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
            registryParam.setTargets(CollUtil.toList(path));
            registryParam.setRecursionLevel(getRegistryRecursionLevel());
            registryParams.add(registryParam);
        }
        return registryParams;
    }
}
