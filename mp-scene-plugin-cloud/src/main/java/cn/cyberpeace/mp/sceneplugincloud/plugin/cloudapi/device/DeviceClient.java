package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.util.UriComponentsBuilder;

public class DeviceClient extends CloudClient {

    private static final String GET_DEVICE_BY_UUID = "/cloud/v1/device/{uuid}";


    public Device findByUuid(String uuid){
        String api = UriComponentsBuilder.fromPath(GET_DEVICE_BY_UUID).buildAndExpand(uuid).toUriString();
        return this.cloudGetWithoutResponseWrapper(api, null, new TypeReference<Device>() {
        }, false);
    }


}
