package cn.cyberpeace.mp.cscene.controller.collect.host;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.sic.HostStateOutput;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

public class WazuhHostInfoDataParser implements WazuhDataParser<WazuhHostStateModel> {
    @Override
    public WazuhHostStateModel parsedOneData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if (processData == null) {
                processData = wazuhFileRoot.getData().getSicLaunchOutput().get("raw_output");
            }
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
            }

            String content = JsonCommon.stringify(wazuhFileRoot.getData().getSicLaunchOutput());
            var hostStateOutput = JSONObject.parseObject(content, new TypeReference<HostStateOutput>() {
            });

            WazuhHostStateModel wazuhHostStateModel = DataCollectionService.convertHostStateOutput2WazuhModel(hostStateOutput);
            wazuhHostStateModel.setAgentName(wazuhFileRoot.getAgent().getName());
            return wazuhHostStateModel;
        }
        return new WazuhHostStateModel();
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getData() != null && SicLaunchConfig.LAUNCH_ID_NET_CONNS.equals(wazuhFileRoot.getData()
                                                                                                  .getSicLaunchId())) {
            return true;
        }
        return false;
    }
}
