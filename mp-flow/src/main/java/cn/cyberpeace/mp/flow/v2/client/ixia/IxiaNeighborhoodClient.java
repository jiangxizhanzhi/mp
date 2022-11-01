package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Slf4j
public class IxiaNeighborhoodClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaNeighborhoodClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void addNeighborhood(BaseCreateData baseCreateData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_NEIGHBORHOOD, baseCreateData, null, CreateNeighborHoodResult.class, new TypeReference<CreateNeighborHoodResult>() {
        });
        log.info("Create neighborhood completed:{}", response);
    }

    public void saveNeighborhood(BaseSaveData baseSaveData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_NEIGHBORHOOD, baseSaveData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        log.info("Save neighborhood completed:{}", response);
    }
}
