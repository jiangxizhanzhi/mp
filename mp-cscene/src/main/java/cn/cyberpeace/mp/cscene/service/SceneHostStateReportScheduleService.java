package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
