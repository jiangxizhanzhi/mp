package cn.cyberpeace.mp.cscene.request;

import lombok.Data;

@Data
public class SshChannelCommandReq {
    private String commandType;
    private String commandContent;
    private String nodeId;
    private String csceneId;
}
