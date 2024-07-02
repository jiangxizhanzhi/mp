package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName MacvlanContainerCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ContainerCreateData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacvlanContainerCreateData extends ContainerCreateData {
    private Integer macvlanId;
    private String cidr;
    private String gatewayIp;
    private String ip;
}
