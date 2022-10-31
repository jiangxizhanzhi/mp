package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Getter
@Setter
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
        var data = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_SUPERFLOW_TO_APPLICATION_PROFILE, addSuperFlowData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        int a = 1;
    }

    public void saveSuperFlow(BaseSaveData baseSaveData) {
        var data = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_APPLICATION_PROFILE, baseSaveData, null, AddSuperFlowResult.class, new TypeReference<AddSuperFlowResult>() {
        });
        int a = 1;
    }
}
