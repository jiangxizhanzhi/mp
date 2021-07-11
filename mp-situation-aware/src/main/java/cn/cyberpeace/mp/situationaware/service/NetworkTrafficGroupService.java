package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.BaseCloudPlatformResult;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest;
import cn.cyberpeace.mp.situationaware.utils.JsonUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Slf4j
public class NetworkTrafficGroupService {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    
    private static String indexPrefix = "mp_";

    private static String ARKIMENETWORKNAME = "oj_ext";

    
    @Async
    public void initArkimeNetwork() {
        log.info("init arkime network");
        try {
            String result = HttpUtils.sendGet(situationAwareConfig.getArkimeCpcloudContainerCreateUrl() + "/networks?fetch_all=true");
            log.info("init arkime network result {}", result);
            JSONArray networkList = (JSONArray) JSONPath.eval(JSON.parse(result), "$.data.list");
            networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> ARKIMENETWORKNAME.equals(json.get("name"))).findFirst().ifPresent(network -> {
                SituationalAwareConstant.ArkimeNetworkId = (String) network.get("id");
                log.info("ArkimeNetworkId {}", SituationalAwareConstant.ArkimeNetworkId);
            });
        } catch (Exception e) {
            log.info("init arkime network error {}", ExceptionUtils.getMessage(e));
        }
    }

    
    public void deleteArkimeInstance(String groupId) {
        
        String result = null;
        try {
            result = HttpUtils.sendGet(situationAwareConfig.getArkimeCpcloudContainerCreateUrl() + "/containers?name=" + groupId);
        } catch (IOException e) {
            log.error("query cloud containers error, {}", ExceptionUtils.getStackTrace(e));
        }
        JSONArray networkList = (JSONArray) JSONPath.eval(JSON.parse(result), "$.data.list");
        AtomicReference<String> containerId = new AtomicReference<>("");
        networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> groupId.equals(json.get("name"))).findFirst().ifPresent(container ->{
            containerId.set((String) container.get("id"));
        });

        
        var deleteParam = new JSONObject();
        var deleteUrl = situationAwareConfig.getArkimeCpcloudContainerCreateUrl() + "/scenarios/container";
        var containerArray = new JSONArray();
        containerArray.add(0, containerId.get());
        deleteParam.put("containers", containerArray);
        try {
            HttpUtils.sendDelete(deleteUrl, deleteParam.toJSONString());

        } catch (IOException e) {
            log.error("deleete  arkime Image instance error, {}",ExceptionUtils.getStackTrace(e));
            log.error(SituationalAwareErrorCode.ARKIME_DELETE_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_DELETE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    
    public String addArkimeImageInstance(GroupModel groupModel) {
        String arkimePrefixId = indexPrefix + String.valueOf(System.currentTimeMillis());
        log.info("index prefix generated {}", arkimePrefixId);
        createArkimeImageInstance(arkimePrefixId);
        return arkimePrefixId;
    }

    
    public void createArkimeImageInstance(String instanceId) {
        if (Objects.isNull(SituationalAwareConstant.ArkimeNetworkId)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, "networkId 为空");
        }
        String url = situationAwareConfig.getArkimeCpcloudContainerCreateUrl() + "/scenarios/container";
        var paramObject = new JSONObject();
        
        paramObject.put("name", instanceId);
        
        paramObject.put("image", situationAwareConfig.getArkimeImageName());
        paramObject.put("cpu", 4);
        paramObject.put("ram", 4096);
        paramObject.put("disk", situationAwareConfig.getArkimeDisk());

        
        JSONArray networks = new JSONArray();
        JSONObject arkimeNetwork = new JSONObject();
        arkimeNetwork.put("network_id", SituationalAwareConstant.ArkimeNetworkId);

        networks.add(0, arkimeNetwork);
        paramObject.put("networks", networks);
        
        JSONObject environment = new JSONObject();
        environment.put("ES_HOST", situationAwareConfig.getArkimeEsHost());
        environment.put("ES_PORT", situationAwareConfig.getArkimeEsPort());
        environment.put("ES_PREFIX", instanceId);
        paramObject.put("environment", environment);

        paramObject.put("interactive", false);
        paramObject.put("run", true);
        paramObject.put("privileged", true);

        try {
            log.info("create arkime Image instance param {}",paramObject.toJSONString());
            var result = HttpUtils.sendPost(url, paramObject.toJSONString());
            BaseCloudPlatformResult  model = JsonUtils.toSnakeObject(result, BaseCloudPlatformResult.class);
            if (Objects.isNull(model) || !model.getCode().equals("0")){
                throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR,model.getMessage());
            }
            log.info("create arkime Image instance res {}", result);
        } catch (Exception e) {
            log.error("create arkime Image instance error {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    
    public void doBindingEntityNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = situationAwareConfig.getArkimeCpcloudNetworkMirrorUrl() + "/openflow/mirror_entity";
        var paramObject = new JSONObject();

        paramObject.put("scene_id", networkTrafficMirrorRequest.getSceneId());
        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        try {
            String res = HttpUtils.sendPost(url, paramObject.toJSONString());
            log.info("dobind Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}, {}", networkTrafficMirrorRequest, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    
    public void doBindingNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = situationAwareConfig.getArkimeCpcloudNetworkMirrorUrl() + "/openflow/mirror";
        var paramObject = new JSONObject();

        paramObject.put("scene_id", networkTrafficMirrorRequest.getSceneId());
        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        paramObject.put("entity", networkTrafficMirrorRequest.getEntity());
        List<String> dstNodes = networkTrafficMirrorRequest.getArkimeInstanceNames();
        paramObject.put("dst_node", dstNodes);
        try {
            String res = HttpUtils.sendPost(url, paramObject.toJSONString());
            log.info("dobind Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}, {}", networkTrafficMirrorRequest, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    
    public void deleteNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = situationAwareConfig.getArkimeCpcloudNetworkMirrorUrl() + "/openflow/mirror";
        var paramObject = new JSONObject();

        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        List<String> dstNodes = networkTrafficMirrorRequest.getArkimeInstanceNames();
        if (Boolean.TRUE.equals(situationAwareConfig.getEntityDst())) {
            dstNodes = List.of(situationAwareConfig.getEntityDstNode());
        }
        paramObject.put("dst_node", dstNodes);
        try {
            String res  = HttpUtils.sendDelete(url, paramObject.toJSONString());
            log.info("delete Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}, {}", networkTrafficMirrorRequest, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    
    public void deleteEntityNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = situationAwareConfig.getArkimeCpcloudNetworkMirrorUrl() + "/openflow/mirror_entity";
        var paramObject = new JSONObject();

        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        List<String> dstNodes = networkTrafficMirrorRequest.getArkimeInstanceNames();
        paramObject.put("dst_node", dstNodes);
        try {
            String res = HttpUtils.sendDelete(url, paramObject.toJSONString());
            log.info("delete Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}, {}", networkTrafficMirrorRequest, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
