package cn.cyberpeace.mp.flow.v2.client.ixia;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/30
 */
public class IxiaSession {
    private String server;
    private String usrname;
    private String password;

    private IxiaLoginClient loginClient;
    private IxiaSupperFlowsClient supperFlowsClient;
    private IxiaCaptureManagerClient captureManagerClient;
    private IxiaTestClient testClient;
    private IxiaApplicationProfileClient applicationProfileClient;

    public IxiaSession(String server, String usrname, String password) {
        this.server = server;
        this.usrname = usrname;
        this.password = password;
        IxiaHttpClient httpClient = new IxiaHttpClient(server, 6000, 10);
        this.loginClient = new IxiaLoginClient(httpClient, usrname, password);
        this.supperFlowsClient = new IxiaSupperFlowsClient(httpClient);
        this.captureManagerClient = new IxiaCaptureManagerClient(httpClient);
        this.testClient = new IxiaTestClient(httpClient);
        this.applicationProfileClient = new IxiaApplicationProfileClient(httpClient);
    }

    public void open() {
        this.loginClient.login();
    }

    public String createApplicationProfile(List<ProtocolFlow> protocolFlowList) {
        // 创建协议流
        for (int i = 1; i <= protocolFlowList.size(); i++) {
            var protocolFlow = protocolFlowList.get(i - 1);
            this.supperFlowsClient.createSuperflow();
            var flows = protocolFlow.getFlowList();
            for (int flowId = 1; flowId <= flows.size(); flowId++) {
                var flow = flows.get(i);
                this.supperFlowsClient.addflow(flow.getName(), "", "");
                var actions = flow.getActions();
                for (int actionId = 1; actionId <= actions.size(); actionId++) {
                    AddActionData addActionData = new AddActionData();
                    addActionData.setSource("");
                    addActionData.setType("");
                    addActionData.setFlowid(flowId);
                    addActionData.setActionid(actionId);
                    this.supperFlowsClient.addAction(addActionData);
                }
            }
            this.supperFlowsClient.saveSuperflow(protocolFlow.getName());
        }
        // 创建 application profile
        // 选择 协议流 并设置权重
        // 保存application profile
        return "";
    }

    public void addApplicationProfile() {
        this.applicationProfileClient.CreateApplicationProfile();
        AddSuperFlowData addSuperFlowData = new AddSuperFlowData();
        AddSuperFlowData.AddSuperFlowInfo addSuperFlowInfo = new AddSuperFlowData.AddSuperFlowInfo();
        addSuperFlowInfo.setSuperflow("zl_super_flow_test_1");
        addSuperFlowInfo.setWeight(100);
        List<AddSuperFlowData.AddSuperFlowInfo> addSuperFlowInfos = new ArrayList<>();
        addSuperFlowInfos.add(addSuperFlowInfo);
        addSuperFlowData.setAdd(addSuperFlowInfos);
        this.applicationProfileClient.addSuperFlow(addSuperFlowData);
        BaseSaveData baseSaveData = new BaseSaveData();
        baseSaveData.setName("zl_app_pro_test_4");
        this.applicationProfileClient.saveSuperFlow(baseSaveData);
    }

    public List<String> createCaptureManager(List<PcapFlow> pcapFlows) {
        // 创建pcap流量
        for (int i = 0; i < pcapFlows.size(); i++) {
            this.captureManagerClient.createPcapCapture(pcapFlows.get(i));
        }
        return new ArrayList<>();
    }

    public String createNeighborhood() {
        return "";
    }

    public String createTest(String neighborhood, String profileName, List<String> capureNames) {
        this.testClient.createTest();
        this.testClient.configNeighborhood(neighborhood);
        this.testClient.configApplicationSimular(profileName);
        this.testClient.configRecreates(capureNames);
        return "";
    }

    public void startTest(String name) {
        this.testClient.saveAndRunTest();
    }

    public void close() {
        this.loginClient.logout();
    }
}
