package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.repo.NetworkAssessmentLogRepo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassName NetworkAssessmentLogService
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 19:52
 **/
@Service
public class NetworkAssessmentLogService {

    private static NetworkAssessmentLogPo NETWORK_LOG = new NetworkAssessmentLogPo();
    private static final String[] ASSESS_TYPE = new String[] {"systematize_scenarios", "mimic_router", "mimic_web", "mimic_cloud", "mimic_dns", "mimic_storage"};

    @Autowired
    private NetworkAssessmentLogRepo networkAssessmentLogRepo;

    /**
     * 初始化networkAssessmentLogPo，这个是有6段内容，
     * 设备上报网络弹性日志时，不一定6段内容每次都会计算，如果没有计算，那就没有内容。
     * 这里先从es里面计算出业务需要展示的弹性日志数据。
     * @author
     * @date 19:15 2023/11/28
     **/
    @PostConstruct
    public void init() {
        for (int i = 0; i < ASSESS_TYPE.length; i++) {
            NetworkAssessmentLogPo typeLogPo = networkAssessmentLogRepo.findLatestNetworkLogByDeviceType(ASSESS_TYPE[i]);
            NetworkAssessmentLogPo searchedLog = Optional.ofNullable(typeLogPo).orElse(new NetworkAssessmentLogPo());
            switch (i) {
                case 0:
                    if (Objects.nonNull(searchedLog.getSystematizeScenarios())) {
                        NETWORK_LOG.setSystematizeScenarios(searchedLog.getSystematizeScenarios());
                    }
                    break;
                case 1:
                    if (Objects.nonNull(searchedLog.getMimicRouter())) {
                        NETWORK_LOG.setMimicRouter(searchedLog.getMimicRouter());
                    }
                    break;
                case 2:
                    if (Objects.nonNull(searchedLog.getMimicWeb())) {
                        NETWORK_LOG.setMimicWeb(searchedLog.getMimicWeb());
                    }
                    break;
                case 3:
                    if (Objects.nonNull(searchedLog.getMimicCloud())) {
                        NETWORK_LOG.setMimicCloud(searchedLog.getMimicCloud());
                    }
                    break;
                case 4:
                    if (Objects.nonNull(searchedLog.getMimicDns())) {
                        NETWORK_LOG.setMimicDns(searchedLog.getMimicDns());
                    }
                    break;
                case 5:
                    if (Objects.nonNull(searchedLog.getMimicStorage())) {
                        NETWORK_LOG.setMimicStorage(searchedLog.getMimicStorage());
                    }
                    break;
                default:
                    break;
            }
        }


    }

    public void save(NetworkAssessmentLogPo networkAssessmentLogPo) {
        BeanUtil.copyProperties(networkAssessmentLogPo, NETWORK_LOG, CopyOptions.create(NetworkAssessmentLogPo.class, true, "id", "reportTime"));
        NETWORK_LOG.setReportTime(LocalDateTime.now());
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

    /**
     * 弹性日志信息，不是每次都会计算
     * @author
     * @date 19:09 2023/11/28
     * @param pageRequest
     * @return org.springframework.data.domain.Page<cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo>
     **/
    public Page<NetworkAssessmentLogPo> page(PageRequest pageRequest) {
        if (pageRequest.getPageSize() == 1) {
            return new PageImpl<>(Collections.singletonList(NETWORK_LOG), pageRequest, 1);
        }
        return networkAssessmentLogRepo.findAll(pageRequest);
    }
}
