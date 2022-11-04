package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/2
 */
@Slf4j
public class IxiaTestStateClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaTestStateClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public TestStatusResult getTestState(GetTestStateData getTestStateData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.GET_TEST_STATE, getTestStateData, null, TestStatusResult.class, new TypeReference<TestStatusResult>() {
        });
        log.info("Get TEST response:{}", response);
        return response;
    }
}
