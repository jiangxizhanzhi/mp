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
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.LOGIN_URL, loginData, null, new TypeReference<LoginResult>() {
        });
        return result;
    }

    public CreateNeighborHoodResult createNeighbor() {
        CreateNeighborHoodData data = new CreateNeighborHoodData();
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_NEIGHBORHOOD, data, null, new TypeReference<CreateNeighborHoodResult>() {
        });
        return result;
    }

    public SaveNeighborHoodResult saveNeighborHood(String name) {
        SaveNeighborHoodData data = new SaveNeighborHoodData();
        data.setName(name);
        try {
            var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_NEIGHBORHOOD, data, null, new TypeReference<SaveNeighborHoodResult>() {
            });
            return result;
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
            return new SaveNeighborHoodResult();
        }
    }
}
