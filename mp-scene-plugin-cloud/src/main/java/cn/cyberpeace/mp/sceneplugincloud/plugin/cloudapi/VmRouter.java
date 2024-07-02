package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.RouterConfigCmdData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SerialPortQueryResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class VmRouter extends CloudClient {

    /**
     * createConfig
     *
     * @param createData
     * @return boolean
     */
    public boolean createConfig(RouterConfigCmdData createData) {
        CloudResponse<Object> response = cloudPost(CloudUrlConstant.ROUTER_CMD_SEND_URL, createData, null, new TypeReference<>() {
        }, true);
        return response.isSuccess();
    }

    /**
     * queryPort
     *
     * @param instanceId
     * @return
     */
    public SerialPortQueryResult queryPort(String instanceId) {
        String url = CloudUrlConstant.GET_SERIAL_PORT_URL.replace("{instance_id}", instanceId);
        CloudResponse<SerialPortQueryResult> response = cloudGet(url, null, new TypeReference<>() {
        }, true);
        return response.getData();
    }

    /**
     * sendCommand
     *
     * @param createData
     * @param vmId
     * @return boolean
     */
    public boolean sendCommand(RouterConfigCmdData createData, String vmId) {
        CloudResponse<Object> response = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_COM_EXEC_CMD.replace("{vm_id}", vmId), createData, null, new TypeReference<>() {
        }, true);
        return response.isSuccess();
    }
}
