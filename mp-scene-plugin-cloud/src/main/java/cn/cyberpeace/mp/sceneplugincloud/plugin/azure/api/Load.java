package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE;

/**
 * @description 载荷操作类
 * @create 2022-09-08 15:03
 */
@Slf4j
public class Load {

    public void create(CloudPluginSceneElementBO cloudElement) {
//        创建任务 /stgj/task/create
        String url = "http://***/stgj/task/create";

//        "taskName，任务名称
        //taskDescribe，任务描述
        //toolId，所使用的工具ID
        //execCommand，访问工具控制页面执行的命令，vmware平台不需要"

        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("taskName", cloudElement.getSrcElement().getEid());
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result;
        Map<String, Object> dataMap;
        try {
            result = HttpUtils.sendHttpPost(url, JSON.toJSONString(formMap), headersMap);
            dataMap = JsonUtils.parseMap(result);
            if (!"0".equals(dataMap.get("code"))) {
                throw new MpRuntimeException("load task create post request failed");
            }
        } catch (IOException e) {
            log.error("{} request error:", url, e);
            throw new MpRuntimeException("load task create post request failed");
        }
        /*"code,创建任务结果，0表示创建成功，1表示创建失败
            taskInfo，任务信息"
        */
        Map data = (Map) dataMap.get("data");
//            回写id
        cloudElement.setResourceId((String) data.get("instanceId"));
//            创建任务成功后启动实例
        String instanceStartUrl = "http://***/stgj/instance/start";
        formMap.clear();
        formMap.put("instanceId", data.get("instanceId"));
        try {
            result = HttpUtils.sendHttpPost(instanceStartUrl, JSON.toJSONString(formMap), headersMap);
            dataMap = JsonUtils.parseMap(result);
            if (!"0".equals(dataMap.get("code"))) {
                throw new MpRuntimeException("load instance start post request failed");
            }
        } catch (IOException e) {
            log.error("{} request error:", url, e);
            throw new MpRuntimeException("load instance start post request failed");
        }
    }

    public void delete(String instanceId) {
        //TODO 删除任务是否需要重新启动实例 taskId
        //删除实例
        sendPost("http://***/stgj/instance/delete", instanceId, "load instance delete post request failed");
        //删除任务
        sendPost("http://***/stgj/task/delete", instanceId, "load task delete post request failed");
    }

    public void execCommand(CloudPluginSceneElementBO element, String command) {

    }

    public void pause(String instanceId) {
        //暂停实例
        sendPost("http://***/stgj/instance/pause", instanceId, "load instance pause post request failed");
    }

    public void restart(String instanceId) {
        //TODO 重置任务是否需要重新启动实例 taskId
        sendPost("http://***/stgj/task/reset", instanceId, "load task reset post request failed");
        //停止实例
        sendPost("http://***/stgj/instance/stop", instanceId, "load instance stop post request failed");
        //启动实例
        sendPost("http://***/stgj/instance/start", instanceId, "load instance start post request failed");


    }

    public void start(String instanceId) {
        //启动实例
        sendPost("http://***/stgj/instance/start", instanceId, "load instance start post request failed");
    }

    private static Map<String, Object> sendPost(String url, String instanceId, String message) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        try {
            String result = HttpUtils.sendHttpPost(url, JSON.toJSONString(formMap), headersMap);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if (!"0".equals(dataMap.get("code"))) {
                throw new MpRuntimeException(message);
            }
            return dataMap;
        } catch (IOException e) {
            log.error("{} request error:", url, e);
            throw new MpRuntimeException(message);
        }
    }

    public void unpause(String instanceId) {
        //恢复实例
        sendPost("http://***/stgj/instance/unpause", instanceId, "load instance unpause post request failed");
    }

    public void stop(String instanceId) {
        //关闭实例
        sendPost("http://***/stgj/instance/stop", instanceId, "load instance stop post request failed");
    }
}
