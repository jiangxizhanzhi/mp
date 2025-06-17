package cn.cyberpeace.mp.sceneplugincollection.service.collect.netifaces;

import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhDataQueryParamsBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig.LAUNCH_ID_INTERFACE;

public class WazuhNetifacesDataQueryParamsBuilder implements WazuhDataQueryParamsBuilder {
    private static final TermQueryBuilder SIC_LAUNCH_ID_MATCH = QueryBuilders.termQuery("data.sic_launch_id", LAUNCH_ID_INTERFACE);

    private List<String> agentNames;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public WazuhNetifacesDataQueryParamsBuilder(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
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

        // 指定端口数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery().should(SIC_LAUNCH_ID_MATCH);
        finalQueryBuilder.must(necessaryQuery);

        // 指定查询时间
        if (startTime != null && endTime != null) {
            BoolQueryBuilder timeQuery = QueryBuilders.boolQuery()
                    .must(this.getTimeRangeQueryBuilder(startTime, endTime));
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
