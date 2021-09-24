package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerDeleteData implements CloudHttpData {
    private List<String> containers;
    private List<String> connections;
    private List<String> portMappings;
    private List<String> qoses;
    private String floatingIp;
}
