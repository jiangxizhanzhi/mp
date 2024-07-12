 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceClient.java.
 // File size 813.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @ClassName DeviceClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class DeviceClient extends CloudClient {

    private static final String GET_DEVICE_BY_UUID = "/cloud/v1/device/{uuid}";


    public Device findByUuid(String uuid){
        String api = UriComponentsBuilder.fromPath(GET_DEVICE_BY_UUID).buildAndExpand(uuid).toUriString();
        return this.cloudGetWithoutResponseWrapper(api, null, new TypeReference<Device>() {
        }, false);
    }


}
