package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

/**
 * @ClassName NetworkAssessmentLogRepoCustomImpl
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/28 20:09
 **/
@Repository
public class NetworkAssessmentLogRepoCustomImpl implements NetworkAssessmentLogRepoCustom {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public NetworkAssessmentLogPo findLatestNetworkLogByDeviceType(String deviceType) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery(deviceType)))
                .withSort(SortBuilders.fieldSort("report_time").order(SortOrder.DESC))
                .withPageable(org.springframework.data.domain.PageRequest.of(0, 1))
                .build();

        SearchHits<NetworkAssessmentLogPo> searchHits = elasticsearchOperations.search(query, NetworkAssessmentLogPo.class);
        return searchHits.isEmpty() ? null : searchHits.getSearchHit(0).getContent();
    }

}
