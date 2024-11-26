package cn.cyberpeace.mp.cscene.controller.collect.process;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

public class WazuhProcessDataParser implements WazuhDataParser<OneRowDataWrapper<WazuhProcessModel>> {

    @Override
    public OneRowDataWrapper<WazuhProcessModel> parsedOneData(WazuhFileRoot wazuhFileRoot) {
        OneRowDataWrapper<WazuhProcessModel> result = new OneRowDataWrapper<WazuhProcessModel>();
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if (processData == null) {
                processData = wazuhFileRoot.getData().getSicLaunchOutput().get("raw_output");
            }
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
                result = JsonCommon.snakeParse(output, new TypeReference<OneRowDataWrapper<WazuhProcessModel>>() {
                });
            }
            for (WazuhProcessModel v : result.getValues()) {
                v.setAgentName(wazuhFileRoot.getAgent().getName());
            }
        }
        return result;
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getData() != null && SicLaunchConfig.LAUNCH_ID_PROCESSES.equals(wazuhFileRoot.getData()
                                                                                                  .getSicLaunchId())) {
            return true;
        }
        return false;
    }
}
