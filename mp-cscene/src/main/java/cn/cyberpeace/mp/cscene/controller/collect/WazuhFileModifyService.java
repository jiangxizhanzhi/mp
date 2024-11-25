package cn.cyberpeace.mp.cscene.controller.collect;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

public class WazuhFileModifyService extends BaseWazuhQueryService<FileEventModel> {

    public WazuhFileModifyService(RestHighLevelClient restHighLevelClient) {
        super(restHighLevelClient);
    }

    @Override
    public BoolQueryBuilder getBizQueryBuilder() {
        return QueryBuilders.boolQuery().should(QueryBuilders.termQuery("rule.groups", "syscheck_file")).should(QueryBuilders.termQuery("data.sic_launch_id", "909651719b054ebca6a3226f18f27431"));
    }

    @Override
    protected FileEventModel parsedOneData(WazuhFileRoot wazuhFileRoot) {
        FileEventModel fileEventModel = new FileEventModel();
        if (wazuhFileRoot.getSyscheck() != null) {
            fileEventModel.setFilePath(wazuhFileRoot.getSyscheck().getPath());
            fileEventModel.setSize(wazuhFileRoot.getSyscheck().getSizeAfter());
            fileEventModel.setUser(wazuhFileRoot.getSyscheck().getUnameAfter());
            fileEventModel.setAction(wazuhFileRoot.getSyscheck().getEvent());
            fileEventModel.setMd5Before(wazuhFileRoot.getSyscheck().getMd5Before());
            fileEventModel.setMd5After(wazuhFileRoot.getSyscheck().getMd5After());
        }

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

        fileEventModel.setFileEventType(FileEventType.FILE);
        fileEventModel.setContent(processContent(wazuhFileRoot));
        fileEventModel.setUpdateTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp()));
        return fileEventModel;
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
