package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Slf4j
public class IxiaCaptureManagerClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaCaptureManagerClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public UploadPcapResult createPcapCapture(PcapFlow pcapFlow) {
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.UPLOAD_PCAP_CAPTURE, pcapFlow, null, UploadPcapResult.class, new TypeReference<UploadPcapResult>() {
        });
        log.info("Upload the pcap file:{}", result);
        return result;
    }
}
