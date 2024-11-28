package cn.cyberpeace.mp.cscene.controller.collect.user;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhSystemUserModel;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class WazuhSystemUserDataParser implements WazuhDataParser<OneRowDataWrapper<WazuhSystemUserModel>> {
    @Override
    public OneRowDataWrapper<WazuhSystemUserModel> parsedOneData(WazuhFileRoot wazuhFileRoot) {
        OneRowDataWrapper<WazuhSystemUserModel> result = new OneRowDataWrapper<WazuhSystemUserModel>();
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if(processData == null){
                processData = wazuhFileRoot.getData().getSicLaunchOutput().get("raw_output");
            }
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
                result = JsonCommon.snakeParse(output, new TypeReference<OneRowDataWrapper<WazuhSystemUserModel>>() {
                });
            }
            result.getValues().forEach(v ->{
                v.setAgentName(wazuhFileRoot.getAgent().getName());
                LocalDateTime localDateTime = wazuhFileRoot.getTimestamp().toInstant()
                        .atZone(ZoneId.systemDefault()) // 使用默认时区
                        .toLocalDateTime();
                v.setTime(localDateTime);
            });
        }
        return result;
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getData() != null && SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS.equals(wazuhFileRoot.getData().getSicLaunchId())) {
            return true;
        }
        return false;
    }
}
