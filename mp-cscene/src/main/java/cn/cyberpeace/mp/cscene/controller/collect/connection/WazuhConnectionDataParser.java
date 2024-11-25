package cn.cyberpeace.mp.cscene.controller.collect.connection;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

public class WazuhConnectionDataParser implements WazuhDataParser<OneRowDataWrapper<WazuhNetworkConnectionModel>> {
    @Override
    public OneRowDataWrapper<WazuhNetworkConnectionModel> parsedOneData(WazuhFileRoot wazuhFileRoot) {
        OneRowDataWrapper<WazuhNetworkConnectionModel> result = new OneRowDataWrapper<WazuhNetworkConnectionModel>();
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if (processData == null) {
                processData = wazuhFileRoot.getData().getSicLaunchOutput().get("raw_output");
            }
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
                result = JsonCommon.snakeParse(output, new TypeReference<OneRowDataWrapper<WazuhNetworkConnectionModel>>() {
                });
            }
        }
        return result;
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getData() != null && SicLaunchConfig.LAUNCH_ID_NET_CONNS.equals(wazuhFileRoot.getData().getSicLaunchId())) {
            return true;
        }
        return false;
    }
}
