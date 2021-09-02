package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class ContainerCreateData implements HttpData {
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
    private String environment;
    private String hostname;
}
