package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.http.HttpUtilsV2;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    @Value("${mp.situationaware.arkime.cpcloud-container-create-url}")
    private String cpCloudUrl;

    @Value("${mp.situationaware.arkime.es-host}")
    private String esHost;

    @Value("${mp.situationaware.arkime.image-name}")
    private String imageName;

    /**
     * 索引前缀
     */
    private static String indexPrefix = "mp_";

    /**
     * 查询底层所有分组数据
     *
     * @return List<GroupModel>
     */
    public List<GroupModel> queryAllGroups() {
        if (ObjectUtils.isEmpty(situationAwareConfig.getGroupUrl())) {
            throw new MpRuntimeException("底层分组服务url不存在，请配置");
        }
        String sceneListString;
        try {
            sceneListString = HttpUtils.sendGet(situationAwareConfig.getGroupUrl());
        } catch (IOException e) {
            log.error("查询所有分组错误:" + e);
            throw new MpRuntimeException("查询分组错误:" + e);
        }
        var jsonObject = JSONObject.parseObject(sceneListString);
        var groupList = jsonObject.getJSONArray("scenes");
        if (CollectionUtils.isEmpty(groupList)) {
            return new ArrayList<>();
        }
        List<String> groupIds = groupList.stream().map(String.class::cast).collect(Collectors.toList());
        return groupIds.stream().map(this::queryGroupById).collect(Collectors.toList());
    }

    /**
     * 根据分组id查询分组
     *
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
     *
     * @param groupId
     */
    public void deleteArkimeInstance(String groupId) {
        //查询groupId对应的实例镜像
        String result = null;
        try {
            result = HttpUtils.sendGet(cpCloudUrl + "/containers");
        } catch (IOException e) {
            log.error("query cloud containers error");
        }
        JSONArray networkList = (JSONArray) JSONPath.eval(JSON.parse(result), "$.data.list");
        AtomicReference<String> containerId = new AtomicReference<>("");
        networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> groupId.equals(json.get("name"))).findFirst().ifPresent(container ->{
            containerId.set((String) container.get("id"));
        });

        //删除实例镜像
        var deleteParam = new JSONObject();
        var deleteUrl = cpCloudUrl + "/scenarios/container";
        var containerArray = new JSONArray();
        containerArray.add(0, containerId.get());
        deleteParam.put("containers", containerArray);
        try {
            HttpUtils.sendDelete(deleteUrl, deleteParam.toJSONString());
        } catch (IOException e) {
            log.error("deleete  arkime Image instance error");
            log.error(SituationalAwareErrorCode.ARKIME_DELETE_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_DELETE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 新增arkime 镜像
     *
     * @param groupModel
     */
    public String addArkimeImageInstance(GroupModel groupModel) {
        String arkimePrefixId = indexPrefix + String.valueOf(System.currentTimeMillis());
        log.info("index prefix generated {}", arkimePrefixId);
        createArkimeImageInstance(arkimePrefixId);
        return arkimePrefixId;
    }

    /**
     * 新增arkime 实例
     *
     * @param instanceId instanceId
     */
    public void createArkimeImageInstance(String instanceId) {
        if (Objects.isNull(SituationalAwareConstant.ArkimeNetworkId)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, "networkId 为空");
        }
        String url = cpCloudUrl + "/scenarios/container";
        var paramObject = new JSONObject();
        //实例名称
        paramObject.put("name", instanceId);
        //docker镜像名称
        paramObject.put("image", imageName);
        paramObject.put("cpu", 2);
        paramObject.put("ram", 2048);

        //设置网络
        JSONArray networks = new JSONArray();
        JSONObject arkimeNetwork = new JSONObject();
        arkimeNetwork.put("network_id", SituationalAwareConstant.ArkimeNetworkId);

        networks.add(0, arkimeNetwork);
        paramObject.put("networks", networks);
        //设置环境变量
        JSONObject environment = new JSONObject();
        environment.put("ES_HOST", esHost);
        environment.put("ES_PREFIX", instanceId);
        paramObject.put("environment", environment);

        paramObject.put("interactive", false);
        paramObject.put("run", true);
        paramObject.put("privileged", true);

        try {
            log.info("create arkime Image instance param {}",paramObject.toJSONString());
            var result = HttpUtils.sendPost(url, paramObject.toJSONString());
        } catch (IOException e) {
            log.error("create arkime Image instance error");
            log.error(SituationalAwareErrorCode.ARKIME_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
