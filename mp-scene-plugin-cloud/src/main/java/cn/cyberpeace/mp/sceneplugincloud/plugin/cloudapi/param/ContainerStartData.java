package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName ContainerStartData
 * @Description
 * @Author zhanglin
 * @Date 2022/10/12
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerStartData implements CloudHttpData {
    // 容器开启
    private String action = "start";
    private boolean waitForStatus = true;
}
