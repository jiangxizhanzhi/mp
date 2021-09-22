package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerCreateData implements CloudHttpData {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private String requestUser;
    private String host;
    private List<VirtualNic> networks;

    private Boolean interactive;
    private Boolean tty;
    private Boolean run;
    private Boolean privilege;
    private Boolean keepNicsSequence;
    private String command;
    private String postCommand;
    private String workdir;
    private Map<String, String> environment;
    private String hostname;

    private String id;
}
