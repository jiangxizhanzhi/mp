package cn.cyberpeace.mp.cscene.controller.collect.filemodify;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataParser;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class WazuhFileModifyDataParser implements WazuhDataParser<FileEventModel> {
    @Override
    public FileEventModel parsedOneData(WazuhFileRoot wazuhFileRoot) {
        FileEventModel fileEventModel = new FileEventModel();
        fileEventModel.setFileEventType(FileEventType.FILE);
        fileEventModel.setContent(processContent(wazuhFileRoot));
        fileEventModel.setUpdateTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp()));
        fileEventModel.setAgentId(wazuhFileRoot.getAgent().getId());
        fileEventModel.setAgentName(wazuhFileRoot.getAgent().getName());
        if (wazuhFileRoot.getRule() != null) {
            fileEventModel.setLevel(wazuhFileRoot.getRule().getLevel());
        }

        if (wazuhFileRoot.getSyscheck() != null) {
            this.parsedOneSyscheckData(wazuhFileRoot, fileEventModel);
        } else {
            this.parsedOneScriptData(wazuhFileRoot, fileEventModel);
        }
        return fileEventModel;
    }

    @Override
    public boolean handlerData(WazuhFileRoot wazuhFileRoot) {
        if (wazuhFileRoot.getRule() != null && wazuhFileRoot.getRule().getGroups() != null && wazuhFileRoot.getRule().getGroups().contains("syscheck_file")) {
            return true;
        }
        if (wazuhFileRoot.getData() != null && SicLaunchConfig.LAUNCH_ID_FILE.equals(wazuhFileRoot.getData().getSicLaunchId())) {
            return true;
        }
        return false;
    }

    private void parsedOneScriptData(WazuhFileRoot wazuhFileRoot, FileEventModel fileEventModel) {
        Object filePathO = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
        if (filePathO == null) {
            filePathO = wazuhFileRoot.getData().getSicLaunchOutput().get("raw_output");
        }
        String filePath = filePathO.toString();
        if (filePath.endsWith("---file_not_exists---")) {
            fileEventModel.setFilePath(StringUtils.replace(filePath, "---file_not_exists---", ""));
            fileEventModel.setDescription("-");
        } else {
            String[] fileSize = filePath.split("---file_size---");
            if (fileSize.length > 0) {
                fileEventModel.setFilePath(fileSize[0]);
            }
            if (fileSize.length > 1) {
                if (NumberUtil.isNumber(fileSize[1])) {
                    fileEventModel.setSize(fileSize[1]);
                }
            }
            fileEventModel.setDescription(StringUtils.EMPTY);
        }
    }

    private void parsedOneSyscheckData(WazuhFileRoot wazuhFileRoot, FileEventModel fileEventModel) {
        fileEventModel.setFilePath(wazuhFileRoot.getSyscheck().getPath());
        fileEventModel.setSize(wazuhFileRoot.getSyscheck().getSizeAfter());
        fileEventModel.setUser(wazuhFileRoot.getSyscheck().getUnameAfter());
        fileEventModel.setAction(wazuhFileRoot.getSyscheck().getEvent());
        fileEventModel.setMd5Before(wazuhFileRoot.getSyscheck().getMd5Before());
        fileEventModel.setMd5After(wazuhFileRoot.getSyscheck().getMd5After());

        if (wazuhFileRoot.getData() != null) {
            if (wazuhFileRoot.getData().getWin() != null && wazuhFileRoot.getData().getWin().getSystem() != null) {
                fileEventModel.setMessage(wazuhFileRoot.getData().getWin().getSystem().getMessage());
            }
            if (wazuhFileRoot.getData().getSca() != null) {
                if (wazuhFileRoot.getData().getSca().getCheck() != null) {
                    fileEventModel.setMessage(wazuhFileRoot.getData().getSca().getCheck().getDescription());
                } else {
                    fileEventModel.setMessage(wazuhFileRoot.getData().getSca().getDescription());
                }
            }
        }
    }

    private JSONObject processContent(WazuhFileRoot wazuhFileRoot) {
        JSONObject jsonObject = new JSONObject();
        WazuhFileRoot.Rule rule = wazuhFileRoot.getRule();
        if (Objects.nonNull(rule)) {
            JSONObject ruleObj = new JSONObject();
            ruleObj.put("firedtimes", rule.getFiredtimes());
            ruleObj.put("description", rule.getDescription());
            ruleObj.put("groups", rule.getGroups());
            ruleObj.put("id", rule.getId());
            jsonObject.put("rule", ruleObj);
        }
        jsonObject.put("full_log", wazuhFileRoot.getFullLog());
        return jsonObject;
    }
}
