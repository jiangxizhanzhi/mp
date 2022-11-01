package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Getter
@Setter
@Slf4j
public class IxiaApplicationProfileClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaApplicationProfileClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public CreateApplicationProfileResult CreateApplicationProfile() {
        BaseCreateData data = new BaseCreateData();
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_APPLICATION_PROFILE, data, null, CreateApplicationProfileResult.class, new TypeReference<CreateApplicationProfileResult>() {
        });
        return new CreateApplicationProfileResult();
    }

    public void addSuperFlow(AddSuperFlowData addSuperFlowData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_SUPERFLOW_TO_APPLICATION_PROFILE, addSuperFlowData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        log.info("Adding super flow to application profile completed:{}", response);
    }

    public void saveSuperFlow(BaseSaveData baseSaveData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_APPLICATION_PROFILE, baseSaveData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        log.info("Save application profile completed:{}", response);
    }
}
