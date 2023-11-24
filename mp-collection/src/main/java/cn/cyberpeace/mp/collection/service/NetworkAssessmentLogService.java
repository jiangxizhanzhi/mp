package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.repo.NetworkAssessmentLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName NetworkAssessmentLogService
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 19:52
 **/
@Service
public class NetworkAssessmentLogService {

    @Autowired
    private NetworkAssessmentLogRepo networkAssessmentLogRepo;

    public void save(NetworkAssessmentLogPo networkAssessmentLogPo) {
        networkAssessmentLogRepo.save(networkAssessmentLogPo);
    }

    public List<NetworkAssessmentLogPo> listLogByTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (Objects.isNull(startTime)) {
            startTime = LocalDateTime.now().minusMinutes(5L);
        }
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        return networkAssessmentLogRepo.findByReportTimeBetweenOrderByReportTimeDesc(startTime, endTime);
    }

    public Page<NetworkAssessmentLogPo> page(PageRequest pageRequest) {
        return networkAssessmentLogRepo.findAll(pageRequest);
    }
}
