package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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

    @Value("${mp.situationaware.arkime.cpcloud-network-mirror-url}")
    private String mirrorUrl;

    @Value("${mp.situationaware.arkime.es-host}")
    private String esHost;

    @Value("${mp.situationaware.arkime.es-port}")
    private String esPort;

    @Value("${mp.situationaware.arkime.image-name}")
    private String imageName;

    /**
     * 索引前缀
     */
    private static String indexPrefix = "mp_";

    private static String ARKIMENETWORKNAME = "oj_ext";

    /**
     * 初始化arkime网络配置
     */
    @Async
    public void initArkimeNetwork() {
        log.info("init arkime network");
        try {
            String result = HttpUtils.sendGet(cpCloudUrl + "/networks?fetch_all=true");
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
        paramObject.put("cpu", 4);
        paramObject.put("ram", 4096);

        //设置网络
        JSONArray networks = new JSONArray();
        JSONObject arkimeNetwork = new JSONObject();
        arkimeNetwork.put("network_id", SituationalAwareConstant.ArkimeNetworkId);

        networks.add(0, arkimeNetwork);
        paramObject.put("networks", networks);
        //设置环境变量
        JSONObject environment = new JSONObject();
        environment.put("ES_HOST", esHost);
        environment.put("ES_PORT", esPort);
        environment.put("ES_PREFIX", instanceId);
        paramObject.put("environment", environment);

        paramObject.put("interactive", false);
        paramObject.put("run", true);
        paramObject.put("privileged", true);

        try {
            log.info("create arkime Image instance param {}",paramObject.toJSONString());
            var result = HttpUtils.sendPost(url, paramObject.toJSONString());
            log.info("create arkime Image instance res {}", result);
        } catch (Exception e) {
            log.error("create arkime Image instance error {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 场景绑定流量镜像
     * @param networkTrafficMirrorRequest
     */
    public void doBindingNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = mirrorUrl + "/openflow/mirror";
        var paramObject = new JSONObject();

        paramObject.put("scene_id", networkTrafficMirrorRequest.getSceneId());
        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        paramObject.put("dst_node", networkTrafficMirrorRequest.getArkimeInstanceNames());
        try {
            String res = HttpUtils.sendPost(url, paramObject.toJSONString());
            log.info("dobind Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}", networkTrafficMirrorRequest);
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 接触场景绑定流量镜像
     * @param networkTrafficMirrorRequest
     */
    public void deleteNetworkTrafficMirror(NetworkTrafficMirrorRequest networkTrafficMirrorRequest) {
        String url = mirrorUrl + "/openflow/mirror";
        var paramObject = new JSONObject();

        paramObject.put("src_vm", networkTrafficMirrorRequest.getVmInstanceIds());
        paramObject.put("src_container", networkTrafficMirrorRequest.getContainerInstanceIds());
        paramObject.put("dst_node", networkTrafficMirrorRequest.getArkimeInstanceNames());
        try {
            String res  = HttpUtils.sendDelete(url, paramObject.toJSONString());
            log.info("delete Network traffic mirror message {}", res);
        } catch (IOException e) {
            log.error("binding mirror error param:{}", networkTrafficMirrorRequest);
            throw new MpRuntimeException(SituationalAwareErrorCode.BINDING_MIRROR_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
