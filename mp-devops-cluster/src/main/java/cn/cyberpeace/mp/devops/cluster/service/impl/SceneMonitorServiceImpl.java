package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.EventMonitorRequest;
import cn.cyberpeace.mp.devops.cluster.request.FlowMonitorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HostMonitorRequest;
import cn.cyberpeace.mp.devops.cluster.response.EventMonitorResponse;
import cn.cyberpeace.mp.devops.cluster.response.FlowMonitorResponse;
import cn.cyberpeace.mp.devops.cluster.response.HostMonitorResponse;
import cn.cyberpeace.mp.devops.cluster.response.SceneBaseResponse;
import cn.cyberpeace.mp.devops.cluster.service.SceneMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description: 场景监控服务类
 * @Autho qiaokang
 * @Date：2021/7/9 09:25
 */
@Service
@Slf4j
public class SceneMonitorServiceImpl implements SceneMonitorService {

    /**
     * 老中台url
     */
    @Value("${mp.base.mg-url}")
    private String mgUrl;

    /**
     * 场景节点监控分页查询
     * @param request
     * @return
     */
    @Override
    public ServiceResult pageHostMonitor(HostMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/host_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID, "perPage"));
            urlBuffer.append(request.getUrlParam());
            res = HttpUtils.sendGet(urlBuffer.toString(), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("pageHostMonitor error:" + e);
            throw new MpRuntimeException("Host monitor error");
        }

        // 结果解析
        SceneBaseResponse<HostMonitorResponse> baseResponse = new SceneBaseResponse<>();
        String result = baseResponse.parseResult(res, HostMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("pageHostMonitor error: " + result);
        }

        return ServiceResult.success(baseResponse);

    }

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    @Override
    public ServiceResult pageFlowMonitor(FlowMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/flow_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID, "perPage", "startTime", "endTime"));
            urlBuffer.append(request.getUrlParam());
            res = HttpUtils.sendGet(urlBuffer.toString(), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("pageFlowMonitor:" + e);
            throw new MpRuntimeException("Flow monitor error");
        }

        // 结果解析
        SceneBaseResponse<FlowMonitorResponse> baseResponse = new SceneBaseResponse<>();
        String result = baseResponse.parseResult(res, FlowMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("pageFlowMonitor: " + result);
        }

        return ServiceResult.success(baseResponse);
    }


    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    @Override
    public ServiceResult pageEventMonitor(EventMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/host_event_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID, "perPage", "startTime", "endTime"));
            urlBuffer.append(request.getUrlParam());
            res = HttpUtils.sendGet(urlBuffer.toString(), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("pageEventMonitor:" + e);
            throw new MpRuntimeException("Event monitor error");
        }

        // 结果解析
        SceneBaseResponse<EventMonitorResponse> baseResponse = new SceneBaseResponse<>();
        String result = baseResponse.parseResult(res, EventMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("pageEventMonitor: " + result);
        }

        return ServiceResult.success(baseResponse);

    }

}
