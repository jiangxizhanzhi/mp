package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import lombok.Data;

@Data
public abstract class CloudClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

}
