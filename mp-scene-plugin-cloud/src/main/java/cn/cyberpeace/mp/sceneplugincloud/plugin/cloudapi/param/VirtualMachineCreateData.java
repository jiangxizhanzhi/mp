package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineCreateData implements CloudHttpData {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private String requestUser;
    private String host;
    private String hostname;
    private String userData;
    private List<VirtualNic> networks;

    private String id;
}
