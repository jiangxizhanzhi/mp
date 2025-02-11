package cn.cyberpeace.mp.cscene.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SshFtpRequest {
    private String host;
    private Integer port;
    private String user;
    private String password;
    private List<String> cmds;
    private String remotePath;
}
