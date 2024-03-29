package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadServiceImpl {

    @Autowired
    private LoadMapper loadMapper;

    /*
     * 载荷页面
     * */
    public List getLoadList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("pageSize",pageSize);
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MpDevopsLoad> list = loadMapper.getLoadList(requestData);
        return list;
    }

    /*
     * 载荷数据同步
     * */
    public Boolean synchronousData() {
        //todo httpClient方式请求 同步数据
        return true;
    }

    /*
     * 修改载荷
     * */
    public Boolean updateLoadByPk(JSONObject requestData) {
        Boolean updateLoadByPk;
        if (requestData != null && requestData.get("pk") != null && requestData.get("pk") instanceof String
                                && !requestData.get("pk").toString().equals("") ) {
            updateLoadByPk = loadMapper.updateLoadByPk(requestData.get("pk").toString(),requestData);
        } else {
            return false;
        }
        return updateLoadByPk;
    }

    /*
     * 删除载荷
     * */
    public Boolean deleteLoadByPk(JSONObject requestData) {
        Boolean deleteLoadByPk;
        if (requestData != null && requestData.get("pk") != null && requestData.get("pk") instanceof String
                && !requestData.get("pk").toString().equals("") ) {
            deleteLoadByPk = loadMapper.deleteLoadByPk(requestData.get("pk").toString());
        } else {
            return false;
        }
        return deleteLoadByPk;
    }

    /*
     * 根据场景id获取绑定的载荷
     * */
    public List getLoadBySceneId(String sceneId) {
        //todo 需要场景载荷关联表
        List loadList = getLoadList(new JSONObject());
        return loadList;
    }

    /*
     * 载荷执行结果统计
     * */
    public Map<String,Integer> getLoadExecutionResultStatistics(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        Map<String,Integer> map = new HashMap();
        //请求次数
        map.put("askTimes",0);
        //成功次数
        map.put("successTimes",0);
        //失败次数
        map.put("failTimes",0);
        //已有结果
        map.put("haveResult",0);
        //暂无结果
        map.put("ooResultForTheTime",0);
        for (int i = 0; i < list.size(); i++) {
            //todo 统计
        }
        return map;
    }

    public List<MpDevopsLoadSceneLog> getLoadExecutionResult(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        return list;
    }

    public Boolean deleteLoadExecutionResultByPk(JSONObject requestData) {
        Boolean deleteLoadExecutionResultByPk;
        if (requestData != null && requestData.get("pk") != null && requestData.get("pk") instanceof String
                && !requestData.get("pk").toString().equals("") ) {
            deleteLoadExecutionResultByPk = loadMapper.deleteLoadExecutionResultByPk(requestData.get("pk").toString());
        } else {
            return false;
        }
        return deleteLoadExecutionResultByPk;
    }

    public Boolean updateLoadExecutionResultByPk(JSONObject requestData) {
        Boolean updateLoadExecutionResultByPk;
        if (requestData != null && requestData.get("pk") != null && requestData.get("pk") instanceof String
                && !requestData.get("pk").toString().equals("") ) {
            updateLoadExecutionResultByPk = loadMapper.updateLoadExecutionResultByPk(requestData.get("pk").toString(),requestData);
        } else {
            return false;
        }
        return updateLoadExecutionResultByPk;
    }

    public Boolean addLoadExecutionResultByPk(JSONObject requestData) {
        Boolean addLoadExecutionResultByPk;
        if (requestData != null) {
            addLoadExecutionResultByPk = loadMapper.addLoadExecutionResultByPk(requestData);
        } else {
            return false;
        }
        return addLoadExecutionResultByPk;
    }

    public Boolean importLoadExecutionResult(MultipartFile file) {
        if(file != null && file.getName() != null && !file.getName().equals("")){
            if(file.getName().endsWith("xlsx") || file.getName().endsWith("xls")){

                return true;
            }
        }
        return false;
    }
}

