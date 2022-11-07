package cn.cyberpeace.mp.flow.v2.client.ixia;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
public interface IxiaUrlConstant {
    String LOGIN_URL = "/api/v1/auth/session";
    String CREATE_NEIGHBORHOOD = "/api/v2/core/network/operations/load";
    String SAVE_NEIGHBORHOOD = "/api/v2/core/network/operations/saveAs";
    String ADD_IP_ROUTER_TO_NEIGHBORHOOD = "/api/v2/core/network/networkModel/ip_router";
    String ADD_INTERFACE_TO_NEIGHBORHOOD = "/api/v2/core/network/networkModel/interface";
    String ADD_STATIC_HOSTS_TO_NEIGHBORHOOD = "/api/v2/core/network/networkModel/ip_static_hosts";
    String ADD_EXTERNAL_HOSTS_TO_NEIGHBORHOOD = "/api/v2/core/network/networkModel/ip_external_hosts";
    String CONFIG_INTERFACE = "/api/v2/core/network/networkModel/interface/Interface_1";
    String CONFIG_STATIC_HOSTS = "/api/v2/core/network/networkModel/ip_static_hosts/Static_Hosts_i1_default";
    String CONFIG_EXTERNAL_HOSTS = "/api/v2/core/network/networkModel/ip_external_hosts/External_Hosts_ext_default";
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
    String CREATE_STRIKE_LIST = "/api/v2/core/strikeList/operations/new";
    String ADD_STRIKE_TO_LIST = "/api/v2/core/strikeList/operations/add";
    String SAVE_STRIKE_LIST = "/api/v2/core/strikeList/operations/save";
    String ADD_SECURITY_TO_TEST = "/api/v2/core/testmodel/operations/add";
    String ADD_STRIKE_LIST_TO_SECURITY = "/api/v2/core/testmodel/component/security_1";
    String CREATE_LOAD = "/api/v2/core/loadProfile/operations/createNewCustom";
    String STRAIN_LOAD = "/api/v2/core/loadProfile/operations/load";
    String ADD_PHASE = "/api/v2/core/loadProfile/phase/add";
    String DELETE_PHASE = "/api/v2/core/loadProfile/phase/%7B0%7D";
    String CHANGE_PHASE = "/api/v2/core/loadProfile/phase/%7B";
    String SAVE_LOAD = "/api/v2/core/loadProfile/operations/save";
}
