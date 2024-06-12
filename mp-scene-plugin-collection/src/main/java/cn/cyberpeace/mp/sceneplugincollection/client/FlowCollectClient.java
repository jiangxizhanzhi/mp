package cn.cyberpeace.mp.sceneplugincollection.client;


import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Mirror;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.ArkimeCreateResBo;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.cyberpeace.mp.common.constant.CommonSingleton.commonAppConfig;

@Data
@Slf4j
public class FlowCollectClient {

    private static final Container containerClient = new Container();
    private static final Mirror mirrorClient = new Mirror();
    private static String indexPrefix = "mp_";
    private static final String ARKIME_IMAGE_NAME = "mp_arkime:v2";



    public ArkimeCreateResBo createArkimeInstance() {
        //arkime在es上创建索引的开头唯一标识
        String arkimePrefixId = indexPrefix + String.valueOf(System.currentTimeMillis());
        log.info("index prefix generated {}", arkimePrefixId);
        if (Objects.isNull(SituationalAwareConstant.ArkimeNetworkId)) {
            throw new MpRuntimeException(SituationalAwareErrorCode.ARKIME_ADD_ERROR, "networkId 为空");
        }
        SituationAwareConfig situationAwareConfig = BeanCommon.getBean(SituationAwareConfig.class);
        String fixedIp = IpUtils.newRandomIps(CloudSingleton.cloudAppConfig.getExternalNetCidr(), 1, Collections.emptyList(), Collections.emptyList(), false).get(0);

        ContainerCreateData arkimeCreateData = new ContainerCreateData();
        arkimeCreateData.setName(arkimePrefixId);
        arkimeCreateData.setImage(situationAwareConfig.getArkimeImageName());
        arkimeCreateData.setCpu(4);
        arkimeCreateData.setRam(4096);
        arkimeCreateData.setDisk(situationAwareConfig.getArkimeDisk());
        VirtualNic virtualNic = new VirtualNic();
        virtualNic.setFixedIp(fixedIp);
        virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
        virtualNic.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
        arkimeCreateData.setNetwork(Collections.singletonList(virtualNic));

        Map<String, String> env = new HashMap<>(8);
        env.put("SIC_ARKIME_ELASTICSEARCH", "http://" + situationAwareConfig.getArkimeEsHost() + ":" + situationAwareConfig.getArkimeEsPort());
        env.put("SIC_ARKIME_INSTALL_DIR", "/opt/arkime");
        env.put("SIC_ARKIME_ELASTICSEARCH_PREFIX", arkimePrefixId);
        env.put("MP_API_HOST", commonAppConfig.getMpIp() + ":" + commonAppConfig.getMpPort());
        env.put("ES_HOST", situationAwareConfig.getArkimeEsHost());
        env.put("ES_PORT", situationAwareConfig.getArkimeEsPort());
        env.put("ES_PREFIX", arkimePrefixId);



        ContainerCreateData createData = containerClient.create(arkimeCreateData);
        ArkimeCreateResBo arkimeCreateResBo = new ArkimeCreateResBo();
        arkimeCreateResBo.setIp(fixedIp);
        arkimeCreateResBo.setResourceId(arkimePrefixId);
        return arkimeCreateResBo;

    }

    public void delArkimeInstance(String instanceId) {
        ContainerDeleteData containerDeleteData = new ContainerDeleteData();
        containerDeleteData.setContainers(Lists.newArrayList(instanceId));
        containerClient.delete(containerDeleteData);
    }

    public void delFlowMirror(String flowMirrorId) {
        mirrorClient.delete(flowMirrorId);
    }








}
