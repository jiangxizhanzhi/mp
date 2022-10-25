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
}
