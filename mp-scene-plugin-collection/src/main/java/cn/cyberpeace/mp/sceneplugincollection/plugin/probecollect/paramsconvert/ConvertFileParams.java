 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConvertFileParams.java.
 // File size 2025.
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
 * @ClassName ConvertFileParams
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see WazuhParamsConvert
 */
public class ConvertFileParams implements WazuhParamsConvert<WazuhControl.ControlFileModifyParam> {

    private Probe probe;

    /**
     * getFileRecursionLevel
     *
     * @return
     */
    private Integer getFileRecursionLevel() {
        return CollectionSingleton.collectionAppConfig.getFileRecursionLevel();
    }

    /**
     * ConvertFileParams
     *
     * @param probe
     */
    public ConvertFileParams(Probe probe) {
        this.probe = probe;
    }

    /**
     * convertParams
     *
     * @return
     */
    @Override
    public List<WazuhControl.ControlFileModifyParam> convertParams() {
        List<WazuhControl.ControlFileModifyParam> fileParams = new ArrayList<>();
        if (probe.getDisabled() || probe.getCollectPath() == null || probe.getCollectPath().isEmpty()) {
            return new ArrayList<>();
        }
        int i = 1;
        // 每有一个路径添加一个参数
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
