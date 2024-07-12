 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneHostStateReportScheduleService.java.
 // File size 1188.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SceneHostStateReportScheduleService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@Slf4j
public class SceneHostStateReportScheduleService {

    private static final Set<String> csceneInstanceIds = new ConcurrentHashSet<>();

    /**
     * addCsceneInstance
     *
     * @param csceneInstanceId
     */
    public static void addCsceneInstance(String csceneInstanceId) {
        csceneInstanceIds.add(csceneInstanceId);
    }

    /**
     * removeCsceneInstance
     *
     * @param csceneInstanceId
     */
    public static void removeCsceneInstance(String csceneInstanceId) {
        csceneInstanceIds.remove(csceneInstanceId);
    }

    /**
     * getCsceneInstanceIds
     *
     * @return
     */
    public List<String> getCsceneInstanceIds() {
        return new ArrayList<String>(csceneInstanceIds);
    }

}
