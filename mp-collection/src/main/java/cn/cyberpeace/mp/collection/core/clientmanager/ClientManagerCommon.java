package cn.cyberpeace.mp.collection.core.clientmanager;

public class ClientManagerCommon {
    public static ClientManager clientManager;

    public ClientManagerCommon(String wazuhUrl, String wazuhUsername, String wazuhPassword, String esServer,
                               String mongoServer) {
        var builder = ClientManager.builder()
                .configWazuh(wazuhUrl, wazuhUsername, wazuhPassword)
                .configEs(esServer)
                .configMgo(mongoServer);
        clientManager = builder.build();
    }
}
