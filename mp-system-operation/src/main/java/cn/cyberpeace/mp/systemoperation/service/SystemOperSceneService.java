package cn.cyberpeace.mp.systemoperation.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import cn.cyberpeace.mp.systemoperation.request.EventMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.FlowMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.HostMonitorRequest;
import cn.cyberpeace.mp.systemoperation.response.EventMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.FlowMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.HostMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.SceneBaseResponse;
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
public class SystemOperSceneService {

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
    public SceneBaseResponse<HostMonitorResponse> pageHostMonitor(HostMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/host_collections");
            // 拼接分页信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, "sceneId"));
            // TODO test
//            res = HttpUtils.sendGet(urlBuffer.toString());
            res = HttpUtils.sendGet("http://10.100.3.4:3000/mock/181/api/sv_scene/scenes/%7Bscene_id%7D/host_collections/");
        } catch (IOException e) {
            log.error("节点监控查询异常:" + e);
            throw new MpRuntimeException("节点监控查询异常:" + e);
        }

        // 结果解析
        SceneBaseResponse<HostMonitorResponse> baseResponse = new SceneBaseResponse();
        baseResponse.parseResult(res, HostMonitorResponse.class);

        return baseResponse;

    }

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    public SceneBaseResponse<FlowMonitorResponse> pageFlowMonitor(FlowMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/flow_collections");
            // 拼接分页信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, "sceneId"));
            // TODO test
//            res = HttpUtils.sendGet(urlBuffer.toString());
            res = HttpUtils.sendGet("http://10.100.3.4:3000/mock/181/api/sv_scene/scenes/%7Bscene_id%7D/flow_collections/");
        } catch (IOException e) {
            log.error("流量监控查询异常:" + e);
            throw new MpRuntimeException("流量监控查询异常:" + e);
        }

        // 结果解析
        SceneBaseResponse<FlowMonitorResponse> baseResponse = new SceneBaseResponse();
        baseResponse.parseResult(res, FlowMonitorResponse.class);

        return baseResponse;

    }

    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    public SceneBaseResponse<EventMonitorResponse> pageEventMonitor(EventMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/flow_collections");
            // 拼接分页信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, "sceneId"));
           // TODO  test
//            res = HttpUtils.sendGet(urlBuffer.toString());
            res = HttpUtils.sendGet("http://10.100.3.4:3000/mock/181/api/sv_scene/scenes/%7Bscene_id%7D/host_event_collections/");
        } catch (IOException e) {
            log.error("事件监控查询异常:" + e);
            throw new MpRuntimeException("事件监控查询异常:" + e);
        }

        // 结果解析
        SceneBaseResponse<EventMonitorResponse> baseResponse = new SceneBaseResponse();
        baseResponse.parseResult(res, EventMonitorResponse.class);

        return baseResponse;

    }

}
