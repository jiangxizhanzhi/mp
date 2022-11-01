package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaSupperFlowsClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaSupperFlowsClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public CreateSuperflowResult createSuperflow() {
        BaseCreateData data = new BaseCreateData();
        ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_SUPERFLOW, data, null, CreateSuperflowResult.class, new TypeReference<CreateSuperflowResult>() {
        });
        return new CreateSuperflowResult();
    }

    public void addflow(String name, String from, String to) {
        AddFlowData data = new AddFlowData();
        AddFlowData.FlowParams params = new AddFlowData.FlowParams();
        params.setName(name);
        params.setFrom(from);
        params.setTo(to);
        data.setFlowParams(params);
        ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_SUPERFLOW, data, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
    }

    public void addAction(AddActionData data) {
        ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_ACTION, data, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
    }

    public CreateSuperflowResult saveSuperflow(String name) {
        BaseSaveData data = new BaseSaveData();
        data.setName(name);
        ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_SUPERFLOW, data, null, CreateSuperflowResult.class, new TypeReference<CreateSuperflowResult>() {
        });
        return new CreateSuperflowResult();
    }
}
