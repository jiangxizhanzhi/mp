package cn.cyberpeace.mp.cscene.controller.collect;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

public class WazuhPortModifyService extends BaseWazuhQueryService<WazuhPortModifyService.WazuhProcessModelWrapper> {

    public WazuhPortModifyService(RestHighLevelClient restHighLevelClient) {
        super(restHighLevelClient);
    }

    @Override
    public BoolQueryBuilder getBizQueryBuilder() {
        return QueryBuilders.boolQuery().should(QueryBuilders.termQuery("data.sic_launch_id", "38949dcf3829489d8dd9e0e63cc66541"));
    }

    @Override
    protected WazuhProcessModelWrapper parsedOneData(WazuhFileRoot wazuhFileRoot) {
        WazuhProcessModelWrapper result = new WazuhProcessModelWrapper();
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
                result = JsonCommon.snakeParse(output, WazuhProcessModelWrapper.class);
            }
        }
        return result;
    }

    @Getter
    @Setter
    public static class WazuhProcessModelWrapper {
        private List<WazuhProcessModel> modifiedPrev;
        private List<WazuhProcessModel> added;
        private List<WazuhProcessModel> values;
        private List<WazuhProcessModel> modified;
        private List<WazuhProcessModel> deletedPrev;
    }

}
