package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 文件变更探针参数转换
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public class ConvertFileParams implements WazuhParamsConvert<WazuhControl.ControlFileModifyParam> {

    private Probe probe;

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
        // 每有一个路径添加一个参数
        for (String path : probe.getCollectPath()) {
            WazuhControl.ControlFileModifyParam fileParam = new WazuhControl.ControlFileModifyParam();
            fileParam.setId(probe.getId() + "_" + i++);
            fileParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
            fileParam.setRealtimeTargets(CollectionUtil.toList(path));
            fileParams.add(fileParam);
        }
        return fileParams;
    }
}
