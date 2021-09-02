package cn.cyberpeace.mp.sceneplugincloud.core.common;

import lombok.Data;

@Data
public abstract class CloudClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

}
