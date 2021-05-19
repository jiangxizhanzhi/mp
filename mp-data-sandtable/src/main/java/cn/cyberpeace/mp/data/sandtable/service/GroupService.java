package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.data.sandtable.config.SandTableConfig;
import cn.cyberpeace.mp.data.sandtable.model.GroupModel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * 流量依赖的组的CRUD
 */
@Service
@Slf4j
public class GroupService {

    @Autowired
    private SandTableConfig sandTableConfig;

    /**
     * 查询底层所有分组数据
     * @return List<GroupModel>
     */
    public List<GroupModel> queryAllGroups(){
        if (ObjectUtils.isEmpty(sandTableConfig.getGroupUrl())) {
            throw new MqRuntimeException("底层分组服务url不存在，请配置");
        }
        String sceneListString;
        try {
            sceneListString = HttpUtils.sendGet(sandTableConfig.getGroupUrl());
        } catch (IOException e) {
            log.error("查询所有分组错误:"+ e);
            throw new MqRuntimeException("查询分组错误:"+ e);
        }
        JSONObject jsonObject = JSONObject.parseObject(sceneListString);
        JSONArray groupList= jsonObject.getJSONArray("scenes");
        if (CollectionUtils.isEmpty(groupList)) {
            return new ArrayList<>();
        }
        List<String> groupIds = groupList.stream().map(String.class::cast).collect(Collectors.toList());
        List<GroupModel> groupModels = groupIds.stream().map(id -> queryGroupById(id)).collect(Collectors.toList());
        return groupModels;
    }

    /**
     * 根据分组id查询分组
     * @param groupId groupId
     * @return GroupModel
     */
    public GroupModel queryGroupById(String groupId) {
        if (ObjectUtils.isEmpty(groupId)) {
            throw new MqRuntimeException("groupId 不为空");
        }

        String requestUri = sandTableConfig.getGroupUrl() + "/" + groupId;

        String res;
        try {
            res = HttpUtils.sendGet(requestUri);
        } catch (IOException e) {
            log.error("查询所有分组错误:"+ e);
            throw new MqRuntimeException("查询分组错误:"+ e);
        }
        JSONObject jsonObject = JSONObject.parseObject(res);

        String groupIdRes = (String) JSONPath.eval(jsonObject, "$.scene.id");
        JSONArray  macIdJsonArray= (JSONArray) JSONPath.eval(jsonObject, "$.scene.macs");
        if (ObjectUtils.isEmpty(macIdJsonArray)) {
            log.error("groupId: {}", groupId);
            throw new MqRuntimeException("查询分组失败");
        }
        List<String> macIds = macIdJsonArray.stream().map(String.class::cast).collect(Collectors.toList());
        return GroupModel.builder().groupId(groupIdRes).macIds(macIds).build();
    }
}
