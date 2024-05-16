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
    private String templateId;
    private String osType;
    private String rootPassword;
    private String displayType;
    private Boolean run;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private String requestUser;
    private String scene;
    private String department;
    private String runHost;
    private String hostname;
    private String userData;
    private List<VirtualNic> network;
    private String hwVifModel;
    private Boolean virtioConsole = false;
    private Boolean mainCloud;
    private Boolean extremeSpeed = true;
    private String customScript;

    private String id;
}
