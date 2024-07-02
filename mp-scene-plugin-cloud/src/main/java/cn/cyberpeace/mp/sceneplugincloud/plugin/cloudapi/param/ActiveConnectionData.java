package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ActiveConnectionData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ActiveConnectionData {

    private List<String> currentConnections;

    private Boolean success;
}
