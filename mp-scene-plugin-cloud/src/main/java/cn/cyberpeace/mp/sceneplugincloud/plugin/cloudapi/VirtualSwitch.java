package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName VirtualSwitch
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class VirtualSwitch extends CloudClient {
    private static final String NETWORK_ID = "{network_id}";


    private static final Logger log = LoggerFactory.getLogger(VirtualSwitch.class);

    /**
     * create
     *
     * @param createData
     * @return
     */
    public VirtualSwitchCreateData create(VirtualSwitchCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualSwitchCreateData> result = cloudPost(CloudUrlConstant.NETWORK_CREATE_URL, createData,
                null, new TypeReference<>() {
                }, true);
        return result.getData();
    }

    /**
     * deleteById
     *
     * @param networkId
     * @return boolean
     */
    public boolean deleteById(String networkId) {
        heavyConcurrentWait();
        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.NETWORK_DELETE_URL.replace(NETWORK_ID, networkId), null, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
        } catch (Exception e) {
            log.error("delete networkId error, networkId is {}", networkId, e);
        }
        return true;
    }

    /**
     * createAcl
     *
     * @param networkAclData
     * @return boolean
     */
    public boolean createAcl(NetworkAclData networkAclData) {
        heavyConcurrentWait();
        try {
            NetworkAclData.NetworkAclDto networkAclDto = new NetworkAclData.NetworkAclDto(networkAclData);
            CloudResponse<Object> result = cloudPost(CloudUrlConstant.NETWORK_ACL_CREATE_URL.replace(NETWORK_ID, networkAclData.getNetworkId()), networkAclDto, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
        } catch (Exception e) {
            log.error("create networkId acl error, networkId is {}", networkAclData, e);
        }
        return true;
    }

    /**
     * deleteAcl
     *
     * @param networkAclData
     * @return boolean
     */
    public boolean deleteAcl(NetworkAclData networkAclData) {
        heavyConcurrentWait();
        try {
            NetworkAclData.NetworkAclDto networkAclDto = new NetworkAclData.NetworkAclDto(networkAclData);
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.NETWORK_ACL_DELETE_URL.replace(NETWORK_ID, networkAclData.getNetworkId()), networkAclDto, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
        } catch (Exception e) {
            log.error("delete networkId acl error, network acl is {}", networkAclData, e);
        }
        return true;
    }

    /**
     * update
     *
     * @param resourceId
     * @param updateData
     */
    public void update(String resourceId, VirtualSwitchUpdateData updateData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPatch(CloudUrlConstant.VIRTUAL_SWITCH_UPDATE_URL.replace("{network_id}", resourceId), updateData, null, new TypeReference<>() {
        }, true);
    }

    public PortMappingCreateResult createPortMapping(PortMappingCreateData portMappingCreateData) {
        PortMappingCreateResult result = getHttpClient().post(CloudUrlConstant.CREATE_PORT_MAPPING, portMappingCreateData, null,
                PortMappingCreateResult.class);

        log.info("portMapping result is {}", JsonCommon.stringify(result));
        return result;
    }

    /**
     * 文件名：Example.java
     * 版本信息：v1.0
     * 日期：2023-04-01
     * 描述：
     * 这是一个示例Java文件，展示了开头注释的标准格式。
     * <p>
     * 版权信息：
     * © 2023 你的名字或公司
     * <p>
     * 修改历史：
     * 2023-04-01 初始版本
     */
    @Getter
    @Setter
    private static class NetworkParam implements CloudHttpData {
        private List<String> networkIds;
    }
}
