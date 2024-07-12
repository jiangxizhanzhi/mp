 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InitiateJobLoader.java.
 // File size 2133.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.base.init;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName InitiateJobLoader
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class InitiateJobLoader {
    public void startJobs(List<MpInitJobInfo> orderedJobInfos) {
        if (CollectionUtils.isEmpty(orderedJobInfos)) {
            log.info("no init job");
            return;
        }
        Set<String> jobId = new HashSet<>();
        for (MpInitJobInfo jobInfo : orderedJobInfos) {
            if (jobId.contains(jobInfo.getJobId())) {
                String detail = "load init job error , init job " + jobInfo.getJobId() + " exists ";
                log.error(detail);
                throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
            }
            jobId.add(jobInfo.getJobId());
        }

        for (MpInitJobInfo jobInfo : orderedJobInfos) {
            log.info("load init job {}, {}, order {}", jobInfo.getJobId(), jobInfo.getDescription(), jobInfo.getOrder());
        }

        for (MpInitJobInfo jobInfo : orderedJobInfos) {
            try {
                log.info("start init job {}, {} ", jobInfo.getJobId(), jobInfo.getDescription());
                jobInfo.getMethodJobWrapper()
                        .action();
                log.info("end init job {}, {} ", jobInfo.getJobId(), jobInfo.getDescription());
            } catch (Exception e) {
                if (!jobInfo.isIgnoreCurrentError()) {
                    throw e;
                } else {
                    String detail = "error init job , job info is " + JsonCommon.stringify(jobInfo);
                    log.error(detail, e);
                }
            }
        }
    }
}
