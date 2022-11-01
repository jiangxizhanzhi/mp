package cn.cyberpeace.mp.flow.v2.client.ixia;

import org.checkerframework.checker.units.qual.A;

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
    private IxiaNeighborhoodClient neighborhoodClient;
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
        this.neighborhoodClient = new IxiaNeighborhoodClient(httpClient);
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
        this.applicationProfileClient.CreateApplicationProfile();
        // 选择 协议流 并设置权重
        List<AddSuperFlowData.AddSuperFlowInfo> addSuperFlowInfos = new ArrayList<>();
        for (int i = 0; i <= protocolFlowList.size(); i++) {
            var protocolFlow = protocolFlowList.get(i);
            AddSuperFlowData.AddSuperFlowInfo addSuperFlowInfo = new AddSuperFlowData.AddSuperFlowInfo();
            addSuperFlowInfo.setSuperflow(protocolFlow.getName());
            addSuperFlowInfo.setWeight(protocolFlow.getWeight());
            addSuperFlowInfos.add(addSuperFlowInfo);
        }
        // 保存application profile
        AddSuperFlowData addSuperFlowData = new AddSuperFlowData();
        addSuperFlowData.setAdd(addSuperFlowInfos);
        this.applicationProfileClient.addSuperFlow(addSuperFlowData);
        BaseSaveData baseSaveData = new BaseSaveData();
        // todo application profile名称如何定义？
        baseSaveData.setName("zl_app_pro_test_999");
        this.applicationProfileClient.saveSuperFlow(baseSaveData);
        return "zl_app_pro_test_999";
    }

    public String addApplicationProfile(String superFlowName) {
        this.applicationProfileClient.CreateApplicationProfile();
        AddSuperFlowData addSuperFlowData = new AddSuperFlowData();
        AddSuperFlowData.AddSuperFlowInfo addSuperFlowInfo = new AddSuperFlowData.AddSuperFlowInfo();
        addSuperFlowInfo.setSuperflow(superFlowName);
        addSuperFlowInfo.setWeight(100);
        List<AddSuperFlowData.AddSuperFlowInfo> addSuperFlowInfos = new ArrayList<>();
        addSuperFlowInfos.add(addSuperFlowInfo);
        addSuperFlowData.setAdd(addSuperFlowInfos);
        this.applicationProfileClient.addSuperFlow(addSuperFlowData);
        BaseSaveData baseSaveData = new BaseSaveData();
        baseSaveData.setName("zl_app_pro_test_999");
        this.applicationProfileClient.saveSuperFlow(baseSaveData);
        return "zl_app_pro_test_999";
    }

    public void addNeighborhood() {
        BaseCreateData baseCreateData = new BaseCreateData();
        baseCreateData.setTemplate("00_neighborhood_server");
        BaseSaveData baseSaveData = new BaseSaveData();
        baseSaveData.setName("zl_nei_test_999");
        this.neighborhoodClient.addNeighborhood(baseCreateData);
        this.neighborhoodClient.saveNeighborhood(baseSaveData);
    }

    public List<String> createCaptureManager(List<PcapFlow> pcapFlows) {
        // 创建pcap流量
        List<String> captureNames = new ArrayList<>();
        for (int i = 0; i < pcapFlows.size(); i++) {
            var result = this.captureManagerClient.createPcapCapture(pcapFlows.get(i));
            captureNames.add(result.getName());
        }
        return captureNames;
    }

    public String createNeighborhood() {
        //todo:暂时用这个，后续再改
        return "BreakingPoint Switching";
    }

    public String createTest(String neighborhood, String profileName, List<String> capureNames) {
        //创建TEST
        BaseCreateData baseCreateData = new BaseCreateData();
        this.testClient.createTest(baseCreateData);
        //向TEST中添加neighborhood
        this.testClient.configNeighborhood(neighborhood);

        AddAppSimToTestData addApplicationProfileToTestData = new AddAppSimToTestData();
        addApplicationProfileToTestData.setName(profileName);
        addApplicationProfileToTestData.setComponent("appsim");
        addApplicationProfileToTestData.setType("appsim");
        addApplicationProfileToTestData.setActive(true);
        AddSuperFlowToAppSimData addSuperFlowToAppSimData = new AddSuperFlowToAppSimData();
        addSuperFlowToAppSimData.setProfile("zl_test_1");
        //todo 协议流先不做
//        this.testClient.configApplicationSimular(addApplicationProfileToTestData, addSuperFlowToAppSimData);
        //向TEST中添加pcap包
        this.testClient.configRecreates(capureNames);
        return "123";
    }

    public void startTest(String name) {
        SaveTestData saveTestData = new SaveTestData();
        RunTestData runTestData = new RunTestData();
        saveTestData.setForce(true);
        saveTestData.setName(name);

        runTestData.setGroup("1");
        runTestData.setModelname(name);
        this.testClient.saveAndRunTest(saveTestData, runTestData);
    }

    public void close() {
        this.loginClient.logout();
    }
}
