package cn.cyberpeace.mp.devopscluster.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CmdListRequest {
    private String host;
    private Integer port;
    private String user;
    private String password;
    private List<String> cmds;
    private String remotePath;
}
