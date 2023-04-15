package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/11/9 15:02
 */
@Slf4j
@Service
public class SceneJsonMockService {

    private static final String INSTANCE_JSON = "config/cscene_instances.json";

    private static final String MONITOR_SCENE_INFO = "config/monitor_scene_info.json";

    private static final String MONITOR_EVENT_AGGR_TOP = "config/monitor_event_aggr_top.json";

    private static final String MONITOR_EVENT_AGGR = "config/monitor_event_aggr.json";

    private static final String LIST_HOST_EVENTS = "config/list_host_events.json";

    private static final String MONITOR_HOSTSTATE_SCENE_TREND = "config/monitor_hoststate_scene_trend.json";

    private static final String MONITOR_SCENE_ALL_ELEMENTS = "config/monitor_scene_all_elements.json";

    private static final String MONITOR_FLOW_RISK_COUNT = "config/monitor_flow_risk_count.json";

    private static final String LIST_FLOWS = "config/list_flows.json";

    private static final String MONITOR_FLOW_SCENE_TREND = "config/monitor_flow_scene_trend.json";


    public List<FlowTrend> listFlowTrend() {
        List<FlowTrend> infos = new ArrayList<>();
        String content = getContent(MONITOR_FLOW_SCENE_TREND);

        if (StringUtils.isNotBlank(content)) {
            infos = JSONObject.parseArray(content, FlowTrend.class);
        }

        return infos;
    }

    public List<FlowAttackDto> listFlows() {
        List<FlowAttackDto> infos = new ArrayList<>();
        String content = getContent(LIST_FLOWS);

        if (StringUtils.isNotBlank(content)) {
            infos = JSONObject.parseArray(content, FlowAttackDto.class);
        }

        return infos;
    }

    public FlowRiskCount getFlowRiskCount() {
        FlowRiskCount info = new FlowRiskCount();
        String content = getContent(MONITOR_FLOW_RISK_COUNT);

        if (StringUtils.isNotBlank(content)) {
            info = JSONObject.parseObject(content, FlowRiskCount.class);
        }

        return info;
    }

    public List<ElementMonitorBasicInfo> listElementMonitorBasicInfos() {
        List<ElementMonitorBasicInfo> infos = new ArrayList<>();
        String content = getContent(MONITOR_SCENE_ALL_ELEMENTS);

        if (StringUtils.isNotBlank(content)) {
            infos = JSONObject.parseArray(content, ElementMonitorBasicInfo.class);
        }

        return infos;
    }


    public List<HostState> listMonitorHoststateSceneTrend() {
        List<HostState> infos = new ArrayList<>();
        String content = getContent(MONITOR_HOSTSTATE_SCENE_TREND);

        if (StringUtils.isNotBlank(content)) {
            infos = JSONObject.parseArray(content, HostState.class);
        }

        return infos;
    }

    public List<CollectionHostEventBO> listHostEvents() {
        List<CollectionHostEventBO> infos = new ArrayList<>();
        String content = getContent(LIST_HOST_EVENTS);

        if (StringUtils.isNotBlank(content)) {
            infos = JSONObject.parseArray(content, CollectionHostEventBO.class);
        }

        return infos;
    }


    public EventAggr getEventAggr() {
        EventAggr info = new EventAggr();
        String content = getContent(MONITOR_EVENT_AGGR);

        if (StringUtils.isNotBlank(content)) {
            info = JSONObject.parseObject(content, EventAggr.class);
        }

        return info;
    }

    public List<ElementEventPercent> getEventAggrTop() {
        List<ElementEventPercent> info = new ArrayList<>();
        String content = getContent(MONITOR_EVENT_AGGR_TOP);

        if (StringUtils.isNotBlank(content)) {
            info = JSONObject.parseArray(content, ElementEventPercent.class);
        }

        return info;
    }

    /**
     * 场景监控信息
     * @return
     */
    public SceneMonitorInfo getSceneMonitorInfo() {
        SceneMonitorInfo info = new SceneMonitorInfo();
        String content = getContent(MONITOR_SCENE_INFO);

        if (StringUtils.isNotBlank(content)) {
            info = JSONObject.parseObject(content, SceneMonitorInfo.class);
        }

        return info;
    }

    /**
     * 场景实例
     * @return
     */
    public CsceneInstanceBO getInstanceBO() {
        CsceneInstanceBO bo = new CsceneInstanceBO();
        String content = getContent(INSTANCE_JSON);

        if (StringUtils.isNotBlank(content)) {
            bo = JSONObject.parseObject(content, CsceneInstanceBO.class);
        }

        return bo;
    }


    private String getContent(String jsonFile) {
        Resource resource = new ClassPathResource(jsonFile);
        String content = null;
        try {
            content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
        } catch (IOException e) {
            log.error("json 解析异常:{}", jsonFile);
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        return content;
    }


}
