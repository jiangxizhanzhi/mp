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
}
