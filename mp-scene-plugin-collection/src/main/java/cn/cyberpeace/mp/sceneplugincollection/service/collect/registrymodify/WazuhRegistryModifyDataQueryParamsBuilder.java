package cn.cyberpeace.mp.sceneplugincollection.service.collect.registrymodify;

import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhDataQueryParamsBuilder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
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
public class WazuhRegistryModifyDataQueryParamsBuilder implements WazuhDataQueryParamsBuilder {
    private static final TermQueryBuilder SYS_CHECK_FILE_MATCH = QueryBuilders.termQuery("rule.groups", "syscheck_registry");
    private static final TermQueryBuilder SIC_LAUNCH_ID_MATCH = QueryBuilders.termQuery("data.sic_launch_id", "5c92e8a3187b40e7b54734c4b03f2000");

    private String filePath;
    private List<String> agentNames;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> levels;
    private boolean single = true;

    public WazuhRegistryModifyDataQueryParamsBuilder(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
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
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery().should(SYS_CHECK_FILE_MATCH)
                .should(SIC_LAUNCH_ID_MATCH);
        finalQueryBuilder.must(necessaryQuery);

        // 指定查询哪些文件路径
        if (StringUtils.isNotEmpty(this.filePath)) {
            BoolQueryBuilder filePathQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("data.sic_launch_output.reg_path", QueryParserUtil.escape(this.filePath)))
                    .should(QueryBuilders.matchQuery("syscheck.path", QueryParserUtil.escape(this.filePath)));
            finalQueryBuilder.must(filePathQuery);
        }

        // 指定查询时间
        if (startTime != null && endTime != null) {
            BoolQueryBuilder timeQuery = QueryBuilders.boolQuery()
                    .must(this.getTimeRangeQueryBuilder(startTime, endTime));
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
