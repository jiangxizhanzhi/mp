package cn.cyberpeace.mp.cscene.controller.collect.multi;

import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;

public class WazuhMultiDataParser implements WazuhDataParser<FileEventModel> {
    @Override
    public FileEventModel parsedOneData(WazuhFileRoot wazuhFileRoot) {
        FileEventModel fileEventModel = new FileEventModel();
        fileEventModel.setAgentName(wazuhFileRoot.getAgent().getName());
        return fileEventModel;
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        return true;
    }
}
