package cn.cyberpeace.mp.cscene.controller.host;

import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataQueryParamsBuilder;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import lombok.Getter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WazuhHostInfoQueryParamsBuilder implements WazuhDataQueryParamsBuilder {
    private static final TermQueryBuilder SIC_LAUNCH_ID_MATCH = QueryBuilders.termQuery("data.sic_launch_id", SicLaunchConfig.LAUNCH_ID_HOST_STATE);

    private List<String> agentNames;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public WazuhHostInfoQueryParamsBuilder(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.agentNames = agentNames;
    }

    @Override
    public BoolQueryBuilder build() {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();

        // 指定节点
        if (!this.agentNames.isEmpty()) {
            finalQueryBuilder.must(QueryBuilders.termsQuery("agent.name", this.agentNames));
        }

        // 指定文件变更数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery().should(SIC_LAUNCH_ID_MATCH);
        finalQueryBuilder.must(necessaryQuery);

        // 指定查询时间
        if (startTime != null && endTime != null) {
            BoolQueryBuilder timeQuery = QueryBuilders.boolQuery().must(this.getTimeRangeQueryBuilder(startTime, endTime));
            finalQueryBuilder.must(timeQuery);
        }

        return finalQueryBuilder;
    }

    @Override
    public List<SortBuilder<?>> sorts() {
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC));
        return sortBuilders;
    }

}
