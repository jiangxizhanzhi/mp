package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VirtualDynamic extends CloudClient {

    public VirtualDynamicCreateData create(VirtualDynamicCreateData createData) {
        heavyConcurrentWait();
        return new VirtualDynamicCreateData();
    }

    public void sendRule(String resourceId, List<String> cmds) {

    }
}
