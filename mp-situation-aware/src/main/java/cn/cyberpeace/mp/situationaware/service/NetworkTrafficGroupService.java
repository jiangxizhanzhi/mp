package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.TrafficGroupModel;
import cn.cyberpeace.mp.situationaware.repository.TrafficGroupModelMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * 流量依赖的组的CRUD
 */
@Service
@Slf4j
public class NetworkTrafficGroupService {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private TrafficGroupModelMapper trafficGroupModelMapper;

    /**
     * 查询底层所有分组数据
     * @return List<GroupModel>
     */
    public List<GroupModel> queryAllGroups(){
        if (ObjectUtils.isEmpty(situationAwareConfig.getGroupUrl())) {
            throw new MpRuntimeException("底层分组服务url不存在，请配置");
        }
        String sceneListString;
        try {
            sceneListString = HttpUtils.sendGet(situationAwareConfig.getGroupUrl());
        } catch (IOException e) {
            log.error("查询所有分组错误:"+ e);
            throw new MpRuntimeException("查询分组错误:"+ e);
        }
        var jsonObject = JSONObject.parseObject(sceneListString);
        var groupList= jsonObject.getJSONArray("scenes");
        if (CollectionUtils.isEmpty(groupList)) {
            return new ArrayList<>();
        }
        List<String> groupIds = groupList.stream().map(String.class::cast).collect(Collectors.toList());
        return groupIds.stream().map(this::queryGroupById).collect(Collectors.toList());
    }

    /**
     * 根据分组id查询分组
     * @param groupId groupId
     * @return GroupModel
     */
    public GroupModel queryGroupById(String groupId) {
//        if (ObjectUtils.isEmpty(groupId)) {
//            throw new MpRuntimeException("groupId 不为空");
//        }
//
//        String requestUri = situationAwareConfig.getGroupUrl() + "/" + groupId;
//
//        String res;
//        try {
//            res = HttpUtils.sendGet(requestUri);
//        } catch (IOException e) {
//            log.error("查询所有分组错误:"+ e);
//            throw new MpRuntimeException("查询分组错误:"+ e);
//        }
//        JSONObject jsonObject = JSONObject.parseObject(res);
//
//        String groupIdRes = (String) JSONPath.eval(jsonObject, "$.scene.id");
//        JSONArray  macIdJsonArray= (JSONArray) JSONPath.eval(jsonObject, "$.scene.macs");
//        String  url= (String) JSONPath.eval(jsonObject, "$.scene.viewer_url");
//        List<String> macIds = macIdJsonArray.stream().map(String.class::cast).collect(Collectors.toList());
//        return GroupModel.builder().groupId(groupIdRes).macIds(macIds).url(url).build();
        return new GroupModel();
    }

    /**
     * 删除流量arkime镜像
     * @param groupId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArkimeInstance(String groupId) {
        //todo 调用云平台接口删除实例
        trafficGroupModelMapper.deleteByTrafficGroupId(groupId);
        return;
    }


    /**
     * 新增分组
     * @param groupModel
     */
    @Transactional(rollbackFor = Exception.class)
    public String addArkimeImageInstance(GroupModel groupModel) {
        String groupId = UUID.randomUUID().toString();
        groupModel.setGroupId(groupId);
        //todo 调用云平台接口新增实例
        TrafficGroupModel trafficGroupModel = new TrafficGroupModel();
        trafficGroupModel.setTrafficGroupId(groupId);
        trafficGroupModelMapper.insert(trafficGroupModel);
        return groupId;
    }
}
