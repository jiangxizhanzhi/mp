package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 脚本探针参数转换
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public class ConvertScriptParams implements WazuhParamsConvert<WazuhControl.ControlProbeParam> {

    private Probe probe;

    private String agentName;

    private boolean isContainer;

    private static String getCollectInterval() {
        return CollectionSingleton.collectionAppConfig.getCollectInterval();
    }

    public ConvertScriptParams(Probe probe, String agentName, Boolean isContainer) {
        this.probe = probe;
        this.agentName = agentName;
        this.isContainer = isContainer;
    }

    @Override
    public List<WazuhControl.ControlProbeParam> convertParams() {
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        String fdfsInnerServer = BaseSingleton.baseAppConfig.getFdfsInnerHttpServer();
        WazuhControl.ControlProbeParam scriptProbeParam = new WazuhControl.ControlProbeParam();
        scriptProbeParam.setId(probe.getId());
        scriptProbeParam.setGroup(agentName);
        List<String> params = new ArrayList<>();
        if (probe.getParams() != null) {
            probe.getParams().forEach(param -> {
                if (param.getValue() != null) {
                    params.add(param.getValue());
                } else {
                    params.add(param.getDefaultValue());
                }
            });
        }
        //参数用空格分开
        if (!CollectionUtil.isEmpty(params)) {
            scriptProbeParam.setScriptParam(StringUtils.joinWith(StringUtils.SPACE, params));
        }
        if (NumberUtils.INTEGER_ZERO.equals(probe.getScriptType())) {
            scriptProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_SH);
        } else if (NumberUtils.INTEGER_ONE.equals(probe.getScriptType())) {
            scriptProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        } else if (NumberUtils.INTEGER_TWO.equals(probe.getScriptType())) {
            scriptProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_BAT);
        }
        scriptProbeParam.setInterval(getCollectInterval());
        scriptProbeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        String url;
        if (isContainer) {
            url = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, fdfsInnerServer, probe.getScriptUrl());
        } else {
            url = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer, probe.getScriptUrl());
        }
        scriptProbeParam.setScriptUrl(url);
        if (probe.getDisabled() != null && probe.getDisabled()) {
            scriptProbeParam.setDisabled(WazuhControl.SCRIPT_DISABLED_YES);
        }
        if (probe.getOutputFormat() != null) {
            scriptProbeParam.setOutputFormat(probe.getOutputFormat());
        }
        return CollectionUtil.toList(scriptProbeParam);
    }
}
