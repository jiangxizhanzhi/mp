package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.BinomialDistribution;

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
    private IxiaTestStateClient testStateClient;
    private IxiaStrikeClient strikeClient;
    private IxiaLoadClient loadClient;

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
        this.testStateClient = new IxiaTestStateClient(httpClient);
        this.strikeClient = new IxiaStrikeClient(httpClient);
        this.loadClient = new IxiaLoadClient(httpClient);
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
        baseSaveData.setName(TextUtils.uuid());
        this.applicationProfileClient.saveSuperFlow(baseSaveData);
        return baseSaveData.getName();
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

    public void createCaptureManager(List<PcapFlow> pcapFlows) {
        // 创建pcap流量
        for (int i = 0; i < pcapFlows.size(); i++) {
            var result = this.captureManagerClient.createPcapCapture(pcapFlows.get(i));
        }
    }

    public String createNeighborhood() {
        // 新建neighborhood
        BaseCreateData baseCreateData = new BaseCreateData();
        // todo 暂时写死要加载的模板
        baseCreateData.setTemplate("zl_nei_tem_1");
        this.neighborhoodClient.addNeighborhood(baseCreateData);
        // 配置neighborhood
//        IpRouterData addIpRouterToNeighborhoodData = new IpRouterData();
//        addIpRouterToNeighborhoodData.setId("99999");
//        addIpRouterToNeighborhoodData.setIpAddress("192.168.1.1");
//        this.neighborhoodClient.addIpRouterToNeighborhood(addIpRouterToNeighborhoodData);
        InterfaceData interfaceData = new InterfaceData();
//        addInterfaceToNeighborhoodData.setId("Interface 2");
//        addInterfaceToNeighborhoodData.setMacAddress("52:52:52:00:00:00");
        interfaceData.setMtu(1200);
//        addInterfaceToNeighborhoodData.setUseVnicMacAddress(true);
//        addInterfaceToNeighborhoodData.setVlanKey("outer_vlan");
//        addInterfaceToNeighborhoodData.setDuplicateMacAddress(true);
//        addInterfaceToNeighborhoodData.setIgnorePauseFrames(false);
//        addInterfaceToNeighborhoodData.setNumber(2);
//        this.neighborhoodClient.addInterfaceToNeighborhood(addInterfaceToNeighborhoodData);
//        this.neighborhoodClient.changeInterface(interfaceData);
        StaticHostsData staticHostsData = new StaticHostsData();
//        staticHostsData.setId("77777");
        staticHostsData.setTags("i2_default");
        staticHostsData.setIpAddress("192.168.1.2");
        staticHostsData.setGatewayIpAddress("192.168.1.254");
//        staticHostsData.setDefaultContainer("Interface 2");
//        staticHostsData.setCount(66534);
//        staticHostsData.setIpSelectionType("random_ip");
//        staticHostsData.setProxy(false);
//        staticHostsData.setPsnNetmask(8);
//        staticHostsData.setGatewayIpAddress("192.168.1.254");
//        staticHostsData.setEnableStats(false);
//        staticHostsData.setBehindSnapt(false);
//        staticHostsData.setNetmask(8);
//        this.neighborhoodClient.addStaticHostsToNeighborhood(addStaticHostsToNeighborhoodData);
        this.neighborhoodClient.changeStaticHosts(staticHostsData);
        ExternalHostsData externalHostsData = new ExternalHostsData();
//        externalHostsData.setId("66666");
//        externalHostsData.setIpAddress("192.168.1.3");
        externalHostsData.setIpAddress("192.168.1.3");
        externalHostsData.setTags("i1_default");
//        this.neighborhoodClient.addExternalHostsToNeighborhood(addExternalHostsToNeighborhoodData);
        this.neighborhoodClient.changeExternalHosts(externalHostsData);
        // 保存neighborhood
        CreateNeighborHoodData createNeighborHoodData = new CreateNeighborHoodData();
        createNeighborHoodData.setName(TextUtils.uuid());
        createNeighborHoodData.setForce(true);
        createNeighborHoodData.setRegenerateOldStyle(true);
        this.neighborhoodClient.saveNeighborhood(createNeighborHoodData);
        return createNeighborHoodData.getName();
    }

    public String createTest(String neighborhood, String profileName, String strikeListName, List<PcapFlow.RecreateData> recreateDatas, String loadName) {
        // 创建TEST
        BaseCreateData baseCreateData = new BaseCreateData();
        this.testClient.createTest(baseCreateData);
        // 向TEST中添加neighborhood
        this.testClient.configNeighborhood(neighborhood);
        // 向TEST中添加Application Simular
        AddAppSimToTestData addApplicationProfileToTestData = new AddAppSimToTestData();
        addApplicationProfileToTestData.setName(profileName);
        addApplicationProfileToTestData.setComponent("appsim");
        addApplicationProfileToTestData.setType("appsim");
        addApplicationProfileToTestData.setActive(true);
        AddSuperFlowToAppSimData addSuperFlowToAppSimData = new AddSuperFlowToAppSimData();
        addSuperFlowToAppSimData.setProfile(profileName);
//        this.testClient.configApplicationSimular(addApplicationProfileToTestData, addSuperFlowToAppSimData);
        // 向TEST中添加攻击流量
        AddSecurityToTestData addSecurityToTestData = new AddSecurityToTestData();
        addSecurityToTestData.setName(strikeListName);
        addSecurityToTestData.setActive(true);
        addSecurityToTestData.setType("security_all");
        addSecurityToTestData.setComponent("security");
        AddStrikeListToSecurityData addStrikeListToSecurityData = new AddStrikeListToSecurityData();
        addStrikeListToSecurityData.setAttackPlan(strikeListName);
        addStrikeListToSecurityData.setAttackProfile("Browser: Low Evasion");
//        this.testClient.configSecurity(addSecurityToTestData, addStrikeListToSecurityData);
        // 向TEST中添加pcap包
        this.testClient.configRecreates(recreateDatas, loadName);
        // 保存TEST
        SaveTestData saveTestData = new SaveTestData();
        saveTestData.setForce(true);
        saveTestData.setName(TextUtils.uuid());
        this.testClient.saveTest(saveTestData);
        return saveTestData.getName();
    }

    public String createStrikeList() {
        this.strikeClient.createStrikeList();

        AddStrikeToListData addStrikeToListData = new AddStrikeToListData();
        AddStrikeToListData.Strike strike = new AddStrikeToListData.Strike();
        strike.setId("/strikes/generic/ixia/apache_1_3_mod_proxy_buffer_overflow_proxy_attack.xml");
        List<AddStrikeToListData.Strike> strikes = new ArrayList<>();
        strikes.add(strike);
        addStrikeToListData.setStrike(strikes);
        this.strikeClient.addStrikeToList(addStrikeToListData);

        SaveStrikeListData saveStrikeListData = new SaveStrikeListData();
        saveStrikeListData.setName(TextUtils.uuid());
        saveStrikeListData.setForce(true);
        this.strikeClient.saveStrikeList(saveStrikeListData);
        return saveStrikeListData.getName();
    }

    public String createLoad(List<TrexClient.LoadInfo> loadList) {
        CreateLoadData createLoadData = new CreateLoadData();
        createLoadData.setLoadProfile(TextUtils.uuid());
        this.loadClient.createLoad(createLoadData);
        this.loadClient.saveLoad();

        BaseCreateData baseCreateData = new BaseCreateData();
        baseCreateData.setTemplate(createLoadData.getLoadProfile());
        for (int i = 0; i < 3; i++) {
            this.loadClient.strainLoad(baseCreateData);
            this.loadClient.deletePhase();
            this.loadClient.saveLoad();
        }

        this.loadClient.strainLoad(baseCreateData);
        IxiaCreatePhaseData ixiaCreatePhaseData = new IxiaCreatePhaseData();
        ixiaCreatePhaseData.setPhaseId("0");
        ixiaCreatePhaseData.setType("up");
        ixiaCreatePhaseData.setDuration(1);
        for (int i = 0; i < loadList.size(); i++) {
            this.loadClient.addPhase(ixiaCreatePhaseData);
        }
        this.loadClient.saveLoad();

        this.loadClient.strainLoad(baseCreateData);
        IxiaPhaseData ixiaPhaseData1 = new IxiaPhaseData();
        for (int i = 0; i < loadList.size(); i++) {
            ixiaPhaseData1.setRateDistunit("fps");
            ixiaPhaseData1.setSessionsmaxPerSecond("1000");
            ixiaPhaseData1.setSessionsmax("1500000");
            ixiaPhaseData1.setRateDistscope("per_if");
            ixiaPhaseData1.setRateDisttype("constant");
            ixiaPhaseData1.setRampDiststeadyBehavior("cycle");
            ixiaPhaseData1.setRateDistmin(String.valueOf(loadList.get(i).getBandwidth()));
            this.loadClient.changePhase(ixiaPhaseData1, i);
        }
        this.loadClient.saveLoad();

        return createLoadData.getLoadProfile();
    }

    public StartTestResult startTest(String name) {
        RunTestData runTestData = new RunTestData();
        runTestData.setGroup("3");
        runTestData.setModelname(name);
        runTestData.setAllowMalware(true);
        return this.testClient.startTest(runTestData);
    }

    public void pauseTest(StopTestData stopTestData) {
        this.testClient.pauseTest(stopTestData);
    }

    public TestStatusResult getTestState(GetTestStateData getTestStateData) {
        return this.testStateClient.getTestState(getTestStateData);
    }

    public void close() {
        this.loginClient.logout();
    }

    public List<TrexClient.LoadInfo> calculateLoad(int time, double maxLoad, String loadMode) {
        List<TrexClient.LoadInfo> loadInfos = new ArrayList<>();
        if (StringUtils.isNotEmpty(loadMode) && loadMode.equals("binomial")) {
            binomialDistributionLoad(loadInfos, time, 0.5, maxLoad);
        }
        return loadInfos;
    }

    private void binomialDistributionLoad(List<TrexClient.LoadInfo> loadInfos, int time, double p, double maxLoad) {
        BinomialDistribution binomialDistribution = new BinomialDistribution(time, p);
        double proportion = maxLoad / binomialDistribution.probability(time / 2);
        for (int i = 1; i <= time; i++) {
            TrexClient.LoadInfo loadInfo = new TrexClient.LoadInfo();
            loadInfo.setBandwidth((int) (binomialDistribution.probability(i) * proportion));
            loadInfo.setTime(i);
            if (loadInfo.getBandwidth() <= 0) {
                loadInfo.setBandwidth(1);
            }
            loadInfos.add(loadInfo);
        }
    }
}
