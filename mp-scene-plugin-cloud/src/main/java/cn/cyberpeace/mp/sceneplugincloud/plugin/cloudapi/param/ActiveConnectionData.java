package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;


@Data
public class ActiveConnectionData {

    private List<String> currentConnections;

    private Boolean success;
}
