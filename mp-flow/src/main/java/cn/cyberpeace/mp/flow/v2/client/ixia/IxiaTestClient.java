package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Slf4j
public class IxiaTestClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaTestClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void createTest(BaseCreateData baseCreateData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_TEST, baseCreateData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("New TEST completed:{}", response);
    }

    public void saveTest(SaveTestData saveTestData) {
        var saveTestRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_TEST, saveTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Save TEST completed:{}", saveTestRes);
    }

    public void configNeighborhood(String neighborhood) {
        AddNeighborhoodToTestData addNeighborhoodToTestData = new AddNeighborhoodToTestData();
        addNeighborhoodToTestData.setNeighborhood(neighborhood);
        var response = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.ADD_NEIGHBORHOOD_TO_TEST, addNeighborhoodToTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add Neighborhood to TEST completed:{}", response);
    }

    public void configApplicationSimular(AddAppSimToTestData addAppSimToTestData, AddSuperFlowToAppSimData addSuperFlowToAppSimData) {
        var addAppSimRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_APPLICATION_SIMULATOR_TO_TEST, addAppSimToTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add application simulator to TEST completed:{}", addAppSimRes);
        var addSuperFlowRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_SUPER_FLOW_TO_APPLICATION_SIMULATOR, addSuperFlowToAppSimData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add super flow to application simulator completed:{}", addSuperFlowRes);
    }

    public void configSecurity(AddSecurityToTestData addSecurityToTestData, AddStrikeListToSecurityData addStrikeListToSecurityDatad) {
        var addSecurityRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_SECURITY_TO_TEST, addSecurityToTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add security to TEST response:{}", addSecurityRes);
        var addStrikeListRes = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.ADD_STRIKE_LIST_TO_SECURITY, addStrikeListToSecurityDatad, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add strike list to security response:{}", addStrikeListRes);
    }

    public void configRecreates(List<PcapFlow.RecreateData> recreateDatas, String loadName) {
        AddPcapToRecreateData.loadProfile loadProfile = new AddPcapToRecreateData.loadProfile();
        loadProfile.setName(loadName);
        if (recreateDatas != null) {
            for (int i = 0; i < recreateDatas.size(); i++) {
                AddRecreateToTestData addRecreateToTestData = new AddRecreateToTestData();
                addRecreateToTestData.setActive(true);
                addRecreateToTestData.setName(recreateDatas.get(i).getName());
                addRecreateToTestData.setType("playback");
                addRecreateToTestData.setComponent("recreate");

                AddPcapToRecreateData addPcapToRecreateData = new AddPcapToRecreateData();
                addPcapToRecreateData.setFile(recreateDatas.get(i).getName());
                addPcapToRecreateData.setDelayStart(recreateDatas.get(i).getDelay());
                addPcapToRecreateData.setLoadprofile(loadProfile);
                var createRecreateRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_RECREATE, addRecreateToTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
                });
                log.info("Create recreate completed:{}", createRecreateRes);
                var addPcapToRecreateRes = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.ADD_PCAP_TO_RECREATE + (i + 1), addPcapToRecreateData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
                });
                log.info("Add pcap file to recreate_{} Complete:{}", (i + 1), addPcapToRecreateRes);
            }
        }
    }

    public StartTestResult startTest(RunTestData runTestData) {
        var runTestRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.RUN_TEST, runTestData, null, StartTestResult.class, new TypeReference<StartTestResult>() {
        });
        log.info("Start executing TEST:{}", runTestRes);
        return runTestRes;
    }

    public void pauseTest(StopTestData stopTestData) {
        var pauseTestRes = ixiaHttpClient.ixiaPost(IxiaUrlConstant.PAUSE_TEST, stopTestData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Pause TEST response:{}", pauseTestRes);
    }
}
