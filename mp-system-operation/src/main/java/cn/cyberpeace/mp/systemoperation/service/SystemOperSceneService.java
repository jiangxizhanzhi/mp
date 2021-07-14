package cn.cyberpeace.mp.systemoperation.service;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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
import java.time.LocalDateTime;

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
    public ServiceResult pageHostMonitor(HostMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/host_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID));
            res = HttpUtils.sendGet(urlBuffer.toString(), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("节点监控查询异常:" + e);
            throw new MpRuntimeException("节点监控查询异常");
        }

        // 结果解析
        SceneBaseResponse<HostMonitorResponse> baseResponse = new SceneBaseResponse();
        String result = baseResponse.parseResult(res, HostMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("场景节点监控信息查询异常: " + result);
        }

        return ServiceResult.success(baseResponse);

    }

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    public ServiceResult pageFlowMonitor(FlowMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/flow_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID));
            res = HttpUtils.sendGet(urlBuffer.toString(), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("流量监控查询异常:" + e);
            throw new MpRuntimeException("流量监控查询异常");
        }

        // 结果解析
        SceneBaseResponse<FlowMonitorResponse> baseResponse = new SceneBaseResponse();
        String result = baseResponse.parseResult(res, HostMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("场景流量监控信息查询异常: " + result);
        }

        return ServiceResult.success(baseResponse);
    }

    public static void main(String[] args) {
        var date = LocalDateTime.now().toString();
        System.out.println(date);


    }

    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    public ServiceResult pageEventMonitor(EventMonitorRequest request) {
        String res;
        try {
            // 拼接url
            StringBuffer urlBuffer = new StringBuffer(mgUrl);
            urlBuffer.append(ControllerConstant.SCENE_API_URL)
                    .append(request.getSceneId())
                    .append("/host_event_collections/");
            // 拼接参数信息
            urlBuffer.append(BeanUtils.wrapperGetParamsByObject(request, ControllerConstant.STR_SCENE_ID));
            // 替换url中的空格
            res = HttpUtils.sendGet(urlBuffer.toString().replace(" ", "%20"), JwtCommon.getTokenHeader());
        } catch (IOException e) {
            log.error("事件监控查询异常:" + e);
            throw new MpRuntimeException("事件监控查询异常");
        }

        // 结果解析
        SceneBaseResponse<EventMonitorResponse> baseResponse = new SceneBaseResponse();
        String result = baseResponse.parseResult(res, EventMonitorResponse.class);
        if (!BaseConstants.STR_SUCCESS.equals(result)) {
            return ServiceResult.fail("场景事件监控信息查询异常: " + result);
        }

        return ServiceResult.success(baseResponse);

    }

}
