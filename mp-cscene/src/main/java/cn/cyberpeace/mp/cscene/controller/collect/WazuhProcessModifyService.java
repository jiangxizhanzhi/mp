package cn.cyberpeace.mp.cscene.controller.collect;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

public class WazuhProcessModifyService extends BaseWazuhQueryService<OneRowDataWrapper<WazuhProcessModel>> {

    public WazuhProcessModifyService(RestHighLevelClient restHighLevelClient) {
        super(restHighLevelClient);
    }

    @Override
    public BoolQueryBuilder getBizQueryBuilder() {
        return QueryBuilders.boolQuery().should(QueryBuilders.termQuery("data.sic_launch_id", "09ffcc4a67ef4accb407a1555629d0f8"));
    }

    @Override
    protected OneRowDataWrapper<WazuhProcessModel> parsedOneData(WazuhFileRoot wazuhFileRoot) {
        OneRowDataWrapper<WazuhProcessModel> result = new OneRowDataWrapper<WazuhProcessModel>();
        if (wazuhFileRoot.getData() != null && wazuhFileRoot.getData().getSicLaunchOutput() != null) {
            Object processData = wazuhFileRoot.getData().getSicLaunchOutput().get("data");
            if (processData != null) {
                String processDataStr = processData.toString();
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(processDataStr, "\""));
                String output = TextUtils.decompress(raw);
                result = JsonCommon.snakeParse(output, OneRowDataWrapper.class);
            }
        }
        return result;
    }

}
