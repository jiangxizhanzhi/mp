package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.cscene.repository.mapper.OutLoadMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
