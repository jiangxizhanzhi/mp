package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaCaptureManagerClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaCaptureManagerClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void createPcapCapture(PcapFlow pcapFlow) {

    }
}
