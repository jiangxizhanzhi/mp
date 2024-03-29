package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.cscene.repository.mapper.OutLoadMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class OutLoadServiceImpl  {

    @Autowired
    private OutLoadMapper outLoadMapper;

    public List getTaskList(JSONObject requestData) {
        return null;
    }

    public Boolean createTask(JSONObject requestData) {
        return null;
    }

    public JSONObject getTaskNet(JSONObject requestData) {
        return null;
    }

    public Boolean updateTaskInfo(JSONObject requestData) {
        return null;
    }

    public Boolean deleteTask(JSONObject requestData) {
        return null;
    }

    public Boolean resetTask(JSONObject requestData) {
        return null;
    }

    public Boolean startInstance(JSONObject requestData) {
        return null;
    }

    public Boolean stopInstance(JSONObject requestData) {
        return null;
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

    public List<JSONObject> getToolList(JSONObject requestData) {
        return null;
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
