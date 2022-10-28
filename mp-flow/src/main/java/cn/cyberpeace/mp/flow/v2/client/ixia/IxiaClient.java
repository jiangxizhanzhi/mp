package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaClient {
    private IxiaHttpClient ixiaHttpClient;

    private String server;

    public IxiaClient(String server) {
        this.server = server;
        this.ixiaHttpClient = new IxiaHttpClient(server, 6000, 10);
    }

    public LoginResult login(String username, String password) {
        LoginData loginData = new LoginData();
        loginData.setUsername(username);
        loginData.setPassword(password);
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.LOGIN_URL, loginData, null, LoginResult.class, new TypeReference<LoginResult>() {
        });
        return result;
    }

    public CreateNeighborHoodResult createNeighbor() {
        BaseCreateData data = new BaseCreateData();
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_NEIGHBORHOOD, data, null, CreateNeighborHoodResult.class, new TypeReference<CreateNeighborHoodResult>() {
        });
        return result;
    }

    public SaveNeighborHoodResult saveNeighborHood(String name) {
        SaveNeighborHoodData data = new SaveNeighborHoodData();
        data.setName(name);
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_NEIGHBORHOOD, data, null, SaveNeighborHoodResult.class, new TypeReference<SaveNeighborHoodResult>() {
        });
        return result;
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
