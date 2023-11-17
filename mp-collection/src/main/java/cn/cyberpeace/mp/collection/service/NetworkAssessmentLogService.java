package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.repo.NetworkAssessmentLogRepo;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class NetworkAssessmentLogService {

    private final NetworkAssessmentLogRepo networkAssessmentLogRepo;

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


        return networkAssessmentLogRepo.findByReportTimeLessThanAndReportTimeGreaterThanOrderByReportTimeDesc(startTime, endTime);
    }
}
