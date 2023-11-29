package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @ClassName CommonDeviceLogRepoCustomImpl
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/29 11:39
 **/
@Repository
public class CommonDeviceLogRepoCustomImpl implements CommonDeviceLogRepoCustom{

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public Page<DeviceLog> pageDeviceLogByQuery(Query query) {

         SearchHits<DeviceLog> searchHits = elasticsearchRestTemplate.search(query, DeviceLog.class);
         if (searchHits.hasSearchHits()) {
             return new PageImpl<>(searchHits.get().map(SearchHit::getContent).collect(Collectors.toList()), query.getPageable(), searchHits.getTotalHits());
         } else {
             return new PageImpl<>(Collections.emptyList(), PageRequest.of(query.getPageable().getPageNumber(), query.getPageable().getPageSize()), searchHits.getTotalHits());
         }
    }
}
