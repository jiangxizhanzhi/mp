package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendSceneElementRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:19
 */
@Slf4j
@Service
public class HostStateService {

    /**
     * 查询单个节点机器当前状态
     *
     * @return
     */
    public List<HostState> getHostStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);


        return null;

    }


    /**
     * 查询单个节点机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getSingleHostStatesTrend(HostStateTrendSceneElementRequest trendRequest) {
        log.info("{}", trendRequest);


        return null;
    }

}
