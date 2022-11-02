package cn.cyberpeace.mp.flow.v2.client.ixia;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
public interface IxiaUrlConstant {
    String LOGIN_URL = "/api/v1/auth/session";
    String CREATE_NEIGHBORHOOD = "/api/v2/core/network/operations/load";
    String SAVE_NEIGHBORHOOD = "/api/v2/sessions/0/bps/network/operations/saveAs";
    String CREATE_SUPERFLOW = "/api/v2/core/superflow/operations/new";
    String ADD_SUPERFLOW = "/api/v2/core/superflow/operations/addFlow";
    String UPDATE_SUPERFLOW = "/api/v2/core/superflow/flows/{flowId}";
    String ADD_ACTION = "/api/v2/core/superflow/operations/addAction";
    String UPDATE_ACTION = "/api/v2/core/superflow/actions/{actionId}";
    String SAVE_SUPERFLOW = "/api/v2/core/superflow/operations/save";
    String CREATE_APPLICATION_PROFILE = "/api/v2/core/appProfile/operations/new";
    String ADD_SUPERFLOW_TO_APPLICATION_PROFILE = "/api/v2/core/appProfile/operations/add";
    String SAVE_APPLICATION_PROFILE = "/api/v2/core/appProfile/operations/save";
    String CREATE_TEST = "/api/v2/core/testmodel/operations/new";
    String SAVE_TEST = "/api/v2/core/testmodel/operations/save";
    String ADD_APPLICATION_SIMULATOR_TO_TEST = "/api/v2/core/testmodel/operations/add";
    String ADD_SUPER_FLOW_TO_APPLICATION_SIMULATOR = "/api/v2/core/testmodel/component/appsim_1";
    String ADD_NEIGHBORHOOD_TO_TEST = "/api/v2/core/testmodel/network";
    String RUN_TEST = "/api/v2/core/testmodel/operations/run";
    String UPLOAD_PCAP_CAPTURE = "/api/v2/core/capture/operations/importCapture";
    String CREATE_RECREATE = "/api/v2/core/testmodel/operations/add";
    String ADD_PCAP_TO_RECREATE = "/api/v2/core/testmodel/component/recreate_";
    String PAUSE_TEST = "/api/v2/core/testmodel/operations/stopRun";
    String GET_TEST_STATE = "/api/v2/core/testmodel/operations/realTimeStats";
}
