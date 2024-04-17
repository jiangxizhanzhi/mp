package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.cscene.repository.mapper.OutLoadMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE;

@Service
public class OutLoadServiceImpl {

    @Autowired
    private OutLoadMapper outLoadMapper;

    public List getTaskList(JSONObject requestData) {
        return null;
    }

    public Map<String, Object> createTask(JSONObject requestData) {

        //todo 根据载荷同步设置获取具体地址、认证和协议
        String url = "http://10.108.1.10:5002/stgj/task/create";
        Map<String, Object> formMap = new HashMap();
        formMap.put("taskName", requestData.get("taskName") != null ? requestData.get("taskName").toString() : "");
        formMap.put("taskDescribe", requestData.get("taskDescribe") != null ? requestData.get("taskDescribe").toString() : "");
        formMap.put("toolId", requestData.get("toolId") != null ? requestData.get("toolId").toString() : "");
        formMap.put("execCommand", requestData.get("execCommand") != null ? requestData.get("execCommand").toString() : "");

        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result = null;
        try {
            result = HttpUtils.sendHttpPost(url, formMap, headersMap);
        } catch (IOException e) {
            return null;
        }
        Map<String, Object> dataMap;
        dataMap = JsonUtils.parseMap(result);
        return dataMap;
    }

    public Map<String, Object> getTaskNet(JSONObject requestData) {
        //todo 根据载荷同步设置获取具体地址、认证和协议
        String url = "http://10.108.1.10:5002/stgj/task/enter";
        Map<String, String> formMap = new HashMap();
        formMap.put("taskId", requestData.get("taskId") != null ? requestData.get("taskId").toString() : "");
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result = null;
        try {
            result = HttpUtils.sendHttpGet(url, formMap, 2000);
        } catch (IOException e) {
            return null;
        }
        Map<String, Object> dataMap;
        dataMap = JsonUtils.parseMap(result);
        return dataMap;
    }

    public Boolean updateTaskInfo(JSONObject requestData) {
        String url = "http://10.108.1.10:5002/stgj/task/modify";
        Map<String, String> formMap = new HashMap();
        formMap.put("id", requestData.get("taskId") != null ? requestData.get("taskId").toString() : "");
        formMap.put("execCommand", requestData.get("param_list") != null ? requestData.get("param_list").toString() : "");
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        try {
            String result = HttpUtils.sendHttpGet(url, formMap, 2000);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if ("0".equals(dataMap.get("code").toString())) {
                return true;
            }
        } catch (IOException e) {
            return null;
        }
        return false;
    }

    public Boolean deleteTask(JSONObject requestData) {
        return null;
    }

    public Boolean resetTask(JSONObject requestData) {
        return null;
    }

    public Boolean startInstance(JSONObject requestData) {
        String url = "http://10.108.1.10:5002/stgj/instance/start";
        Map<String, String> formMap = new HashMap();
        formMap.put("load_id", requestData.get("instanceId") != null ? requestData.get("instanceId").toString() : "");
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        try {
            String result = HttpUtils.sendHttpGet(url, formMap, 2000);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if ("0".equals(dataMap.get("code").toString())) {
                return true;
            }
        } catch (IOException e) {
            return null;
        }
        return false;
    }

    public Boolean stopInstance(JSONObject requestData) {
        String url = "http://10.108.1.10:5002/stgj/instance/stop";
        Map<String, String> formMap = new HashMap();
        formMap.put("load_id", requestData.get("instanceId") != null ? requestData.get("instanceId").toString() : "");
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        try {
            String result = HttpUtils.sendHttpGet(url, formMap, 2000);
            Map<String, Object> dataMap = JsonUtils.parseMap(result);
            if ("0".equals(dataMap.get("code").toString())) {
                return true;
            }
        } catch (IOException e) {
            return null;
        }
        return false;
    }

    public Boolean pauseInstance(JSONObject requestData) {
        return null;
    }

    public Boolean unpauseInstance(JSONObject requestData) {
        return null;
    }

    public Boolean deleteInstance(JSONObject requestData) {
        return null;
    }

    public JSONObject getInstanceById(JSONObject requestData) {
        return null;
    }

    public List getToolList(JSONObject requestData) {
        //todo 根据载荷同步设置获取具体地址、认证和协议
        String url = "http://10.108.1.10:5002/stgj/tool/list";
        Map<String, String> formMap = new HashMap();
        formMap.put("pagenum", requestData.get("pagenum") != null ? requestData.get("pagenum").toString() : "1");
        formMap.put("pagesize", requestData.get("pagesize") != null ? requestData.get("pagesize").toString() : "20");
        formMap.put("keyword", requestData.get("keyword") != null ? requestData.get("keyword").toString() : "");
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        List dataList = new ArrayList();
        String result = null;
        try {
            result = HttpUtils.sendHttpGet(url, formMap, 2000);
        } catch (IOException e) {
            return dataList;
        }
        Map<String, Object> dataMap;
        dataMap = JsonUtils.parseMap(result);
        if ("0".equals(dataMap.get("code").toString())) {
            LinkedHashMap data = (LinkedHashMap) dataMap.get("data");
            if (data != null && data.get("toolList") != null && data.get("toolList") instanceof List) {
                dataList = (List) data.get("toolList");
            }
        }
        return dataList;
    }

    public Boolean addTool(JSONObject requestData) {
        return null;
    }

    public Boolean updateToolInfo(JSONObject requestData) {
        return null;
    }

    public Boolean uploadTool(MultipartFile file) {
        return null;
    }

    public Boolean deleteTool(JSONObject requestData) {
        return null;
    }

    public JSONObject checkNameVaild(JSONObject requestData) {
        return null;
    }

    public JSONObject getReadmeByToolId(JSONObject requestData) {
        return null;
    }

    public Boolean uploadReadme(MultipartFile file) {
        return null;
    }
}
