package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.service.impl.FdfsRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Slf4j
public class TrexClient {
    private String ip;
    private String port;

    public TrexNic createNic() {
        return new TrexNic();
    }

    public void initTrexServer(InitInfo initInfo) {
        String server = ip + ":" + port;
        try {
            var initTrexResponse = HttpUtils.sendPost("http://" + server + "/api/v1/task/network/init",
                    JsonUtils.snakeStringify(initInfo));
        } catch (Exception e) {
            log.error("Failed to initialize trex virtual machine", e);
        }
    }

    public void uploadResource(List<FlowResource> resources) {
        String server = ip + ":" + port;
        resources.stream().forEach(resource -> {
            try {
                var uploadPcapResponse = HttpUtils.sendPost("http://" + server + "/api/v1/resource/pcap/upload",
                        JsonUtils.snakeStringify(resource));
                log.info(uploadPcapResponse);
            } catch (Exception e) {
                log.error("Failed to upload pcap file, pcapFile is {}, flowId is {}", resource.getPcapFile(), resource.getFlowId());
            }
        });
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

    @Getter
    @Setter
    public static class InitInfo {
        private String mac;
        private String taskId;
    }
}
