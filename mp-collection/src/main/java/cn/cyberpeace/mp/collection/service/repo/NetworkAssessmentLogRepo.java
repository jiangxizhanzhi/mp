package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName NetworkAssessmentLogRepo
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 19:44
 **/
@Repository
public interface NetworkAssessmentLogRepo extends ElasticsearchRepository<NetworkAssessmentLogPo,String> {

    List<NetworkAssessmentLogPo> findByReportTimeBetweenOrderByReportTimeDesc(LocalDateTime startTime, LocalDateTime endTime);



}
