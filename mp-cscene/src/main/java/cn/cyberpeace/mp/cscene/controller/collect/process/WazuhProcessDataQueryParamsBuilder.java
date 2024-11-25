package cn.cyberpeace.mp.cscene.controller.collect.process;

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
public class WazuhProcessDataQueryParamsBuilder implements WazuhDataQueryParamsBuilder {

    private static final TermQueryBuilder SIC_LAUNCH_ID_MATCH = QueryBuilders.termQuery("data.sic_launch_id", SicLaunchConfig.LAUNCH_ID_PROCESSES);

    private List<String> agentNames;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> levels;

    public WazuhProcessDataQueryParamsBuilder(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        this.agentNames = agentNames;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public BoolQueryBuilder build() {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();

        finalQueryBuilder.must(SIC_LAUNCH_ID_MATCH);

        // 指定节点
        if (!this.agentNames.isEmpty()) {
            finalQueryBuilder.must(QueryBuilders.termsQuery("agent.name", this.agentNames));
        }

        // 指定查询时间
        if (startTime != null && endTime != null) {
            BoolQueryBuilder timeQuery = QueryBuilders.boolQuery().must(this.getTimeRangeQueryBuilder(startTime, endTime));
            finalQueryBuilder.must(timeQuery);
        }

        // 指定事件等级
        if (this.levels != null) {
            finalQueryBuilder.must(this.getLevelQueryBuilder(this.levels));
        }

        return finalQueryBuilder;
    }

    @Override
    public List<SortBuilder<?>> sorts() {
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC));
        return sortBuilders;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
