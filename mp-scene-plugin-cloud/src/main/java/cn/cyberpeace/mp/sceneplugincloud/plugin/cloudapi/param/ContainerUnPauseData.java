package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerUnPauseData implements CloudHttpData {
    // 容器无法挂起,从挂起恢复时相当于从暂停恢复
    private String action = "unpause";
    private boolean waitForStatus = true ;
}
