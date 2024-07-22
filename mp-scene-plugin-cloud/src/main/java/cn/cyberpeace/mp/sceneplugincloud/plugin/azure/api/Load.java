package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.LoadUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
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

    private String loadUrl;

    public Load(DevopsLoadSourceModel devopsLoadSourceModel) {
        loadUrl = devopsLoadSourceModel.getIp();
    }


    public void create(CloudScenePluginHandler pluginHandler,CloudPluginSceneElementBO cloudElement) {

        String groupName = pluginHandler.getSourceSceneId();
        String nodeId = cloudElement.getSrcElement().getId();
        cloudElement.setLoadTaskName(groupName+"_"+nodeId);
        cloudElement.setLoadTaskDescribe(groupName+"_"+nodeId);
        Map data = create(cloudElement.getLoadTaskName(),
                cloudElement.getLoadTaskDescribe(),
                cloudElement.getLoadToolId(),
                cloudElement.getLoadExecCommand());
//            回写id
        cloudElement.setResourceId(data.get("id").toString());
        cloudElement.setLoadResourceInstanceId(data.get("instanceId").toString());

//        启动实例
        start(data.get("instanceId").toString());
    }

    public Map create(String taskName, String taskDescribe, String toolId, String execCommand) {
        // 创建任务
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("taskName", taskName);
        formMap.put("taskDescribe", taskDescribe);
        formMap.put("toolId", toolId);
        formMap.put("execCommand", execCommand);
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result;
        Map<String, Object> dataMap;
        try {
            String s = loadUrl + LoadUrlConstant.CREATE_TASK_URL;
            result = HttpUtils.sendHttpPost(s, JSON.toJSONString(formMap), headersMap);
            dataMap = JsonUtils.parseMap(result);

            if (!"0".equals(dataMap.get("code").toString())) {
                throw new MpRuntimeException("load task create post request failed");
            }
        } catch (IOException e) {
            log.error("{} request error:", loadUrl + LoadUrlConstant.CREATE_TASK_URL, e);
            throw new MpRuntimeException("load task create post request failed");
        }

        Map data = (Map) dataMap.get("data");
        return data;
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", cloudElement.getLoadResourceInstanceId());
        //删除实例
        sendPost(loadUrl + LoadUrlConstant.DELETE_INSTANCE_URL, JSON.toJSONString(formMap), "load instance delete post request failed");
        //删除任务
        String resourceId = cloudElement.getResourceId();
        String body = JSON.toJSONString(new String[]{resourceId});

        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        try {
            String result = HttpUtils.sendHttpPost(loadUrl + LoadUrlConstant.DELETE_TASK_URL, body, headersMap);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if (!"0".equals(dataMap.get("code").toString())) {
                throw new MpRuntimeException("load task delete post request failed");
            }
        } catch (IOException e) {
            log.error("{} request error:", loadUrl + LoadUrlConstant.DELETE_TASK_URL, e);
            throw new MpRuntimeException("load task delete post request failed");
        }
    }

    public void execCommand(CloudPluginSceneElementBO element, String command) {

    }

    public void pause(String instanceId) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        //暂停实例
        sendPost(loadUrl + LoadUrlConstant.PAUSE_INSTANCE_URL, JSON.toJSONString(formMap), "load instance pause post request failed");
    }

    public void restart(String instanceId) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        //停止实例
        sendPost(loadUrl + LoadUrlConstant.STOP_INSTANCE_URL, JSON.toJSONString(formMap), "load instance stop post request failed");
        //启动实例
        sendPost(loadUrl + LoadUrlConstant.START_INSTANCE_URL, JSON.toJSONString(formMap), "load instance start post request failed");

    }

    public void start(String instanceId) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        //启动实例
        sendPost(loadUrl + LoadUrlConstant.START_INSTANCE_URL, JSON.toJSONString(formMap), "load instance start post request failed");
    }

    private static Map<String, Object> sendPost(String url, String body, String message) {
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        try {
            String result = HttpUtils.sendHttpPost(url, body, headersMap);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if (!"0".equals(dataMap.get("code").toString())) {
                throw new MpRuntimeException(message);
            }
            return dataMap;
        } catch (IOException e) {
            log.error("{} request error:", url, e);
            throw new MpRuntimeException(message);
        }
    }

    public void unpause(String instanceId) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        //恢复实例
        sendPost(loadUrl + LoadUrlConstant.UNPAUSE_INSTANCE_URL, JSON.toJSONString(formMap), "load instance unpause post request failed");
    }

    public void stop(String instanceId) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("instanceId", instanceId);
        //关闭实例
        sendPost(loadUrl + LoadUrlConstant.STOP_INSTANCE_URL, JSON.toJSONString(formMap), "load instance stop post request failed");
    }

    public void recreate(CloudPluginSceneElementBO element) {
        HashMap<String, Object> formMap = new HashMap<>();
        formMap.put("taskId", element.getResourceId());
        //重置任务
        sendPost(loadUrl + LoadUrlConstant.RESET_TASK_URL, JSON.toJSONString(formMap), "load task reset post request failed");
        formMap.clear();
        formMap.put("instanceId", element.getLoadResourceInstanceId());
        //启动实例
        sendPost(loadUrl + LoadUrlConstant.START_INSTANCE_URL, JSON.toJSONString(formMap), "load instance start post request failed");
    }
}
