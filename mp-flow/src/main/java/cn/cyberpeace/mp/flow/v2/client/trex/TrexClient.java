package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.service.impl.FdfsRemoteFileServiceImpl;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
public class TrexClient {
    private String ip;
    private String port;

    public TrexNic createNic() {
        return new TrexNic();
    }

    public void initTrexServer() {

    }

    public void uploadResource(List<FlowResource> resources) {

    }

    public void startTask(List<ProtocolInfo> protocolInfos, List<LoadInfo> loadInfos) {

    }

    public static class ProtocolInfo {

    }

    @Getter
    @Setter
    public static class LoadInfo {
        private Integer time;
        private Integer load;
    }

    @Getter
    @Setter
    public static class FlowResource {
        private String flowId;
        private String pcapFile;
    }
}
