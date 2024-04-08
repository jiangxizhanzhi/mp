package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public interface WazuhDataQueryParamsBuilder {
    BoolQueryBuilder build();

    List<SortBuilder<?>> sorts();

    default BoolQueryBuilder getLevelQueryBuilder(List<Integer> levels) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolean lowLevel = levels.get(0) <= 4;
        if (lowLevel) {
            // 规则级别匹配条件
            // 比较奇怪,不知道为什么这么查 copy from cn.cyberpeace.mp.situationaware.service.DataCollectionService#buildLevelQuery
            boolQueryBuilder.should(QueryBuilders.termsQuery("rule.level", levels));

            // 规则级别字段不存在条件：级别字段不存在，默认为低等级
            ExistsQueryBuilder ruleLevelExistsQuery = QueryBuilders.existsQuery("rule.level");
            boolQueryBuilder.should(QueryBuilders.boolQuery().mustNot(ruleLevelExistsQuery));
            boolQueryBuilder.minimumShouldMatch(1);
        } else {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.level", levels));
        }
        return boolQueryBuilder;
    }

    default BoolQueryBuilder getTimeRangeQueryBuilder(LocalDateTime startTime, LocalDateTime endTime) {
        BoolQueryBuilder timeQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery(SituationalAwareConstant.ES_TIMESTAMP_FIELD)
                        .from(startTime.atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter))
                        .to(endTime.atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter)));
        return timeQuery;
    }
}
