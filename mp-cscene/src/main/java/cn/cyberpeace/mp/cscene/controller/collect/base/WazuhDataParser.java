package cn.cyberpeace.mp.cscene.controller.collect.base;

import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;

public interface WazuhDataParser<ParsedResult> {
    ParsedResult parsedOneData(WazuhFileRoot wazuhFileRoot);

    boolean handlerData(WazuhFileRoot wazuhFileRoot);
}
