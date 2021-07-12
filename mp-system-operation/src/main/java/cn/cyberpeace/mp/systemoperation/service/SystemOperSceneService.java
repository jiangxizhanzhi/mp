package cn.cyberpeace.mp.systemoperation.service;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.systemoperation.request.HostMonitorRequest;
import cn.cyberpeace.mp.systemoperation.response.HostMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.Links;
import cn.cyberpeace.mp.systemoperation.response.SceneBaseResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
            urlBuffer.append("/api/sv_scene/scenes/")
                    .append(request.getSceneId())
                    .append("/host_collections");
            // 拼接分页信息
            urlBuffer.append("?")
                    .append("page=")
                    .append(Objects.nonNull(request.getPage()) ? request.getPage() : BaseConstants.DEFAULTPAGE);
            urlBuffer.append("&")
                    .append("per_page=")
                    .append(Objects.nonNull(request.getPerPage()) ? request.getPerPage() : BaseConstants.DEFAULTPAGESIZE);

            res = HttpUtils.sendGet(urlBuffer.toString());
        } catch (IOException e) {
            log.error("节点监控查询异常:" + e);
            throw new MpRuntimeException("节点监控查询异常:" + e);
        }

        // 结果解析
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject linkObj = jsonObject.getJSONObject("links");
        Links links = JSONObject.toJavaObject(linkObj, Links.class);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        List<HostMonitorResponse> monitorResponseList = JSONArray.parseArray(dataArray.toJSONString(), HostMonitorResponse.class);
        SceneBaseResponse<HostMonitorResponse> baseResponse = new SceneBaseResponse();
        baseResponse.setData(monitorResponseList);
        baseResponse.setLinks(links);
        return baseResponse;

    }

}
