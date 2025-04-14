package cn.cyberpeace.mp.cscene.service.multirange.testrange;

public interface TestRangeApiUrl {
    //登录认证
    public static final String AUTH_TOKEN = "/api/mp/auth/login";

    public static final String BASIC_ELEMENT_MONITOR_INFO = "/api/mp/cscene/monitor/scene/all_elements/%s";

    public static final String AUTH_HEALTH_CHECK = "/api/mp/auth/user_permissions/menuidents/byloginuser";

    public static final String MAP_SCENE_ID = "/api/mp/cscene/cscenes/get_scene_id_by_vpn_server_ip";
    public static final String ELEMENT_OS = "/api/mp/cscene/monitor/scene/element/os";

    public static final String ELEMENT_TREND_STATE = "/api/mp/cscene/monitor/hoststate/element_trend";
    public static final String ELEMENT_STATE = "/api/mp/cscene/monitor/hoststate";
    public static final String ELEMENT_NET_INTERFACE = "/api/mp/cscene/monitor/hoststate/element_netifaces";
    public static final String ELEMENT_PORT = "/api/mp/cscene/monitor/hoststate/element_port";
    public static final String ELEMENT_PROCESS = "/api/mp/cscene/monitor/hoststate/element_process";
    public static final String ELEMENT_CONNECT = "/api/mp/cscene/monitor/scene/element/connection_state";
    public static final String FILE_MODIFY = "/api/mp/cscene/monitor/scene/element/file_modify";
    public static final String REGISTRY_MODIFY = "/api/mp/cscene/monitor/scene/element/registry_modify";
    public static final String LIST_HOST_EVENTS = "/api/mp/cscene/cscene_instances/list_host_events";
    public static final String SCENE_TREND = "/api/mp/cscene/monitor/hoststate/scene_trend";

    public static final String FLOW_ATTACK_PAGE = "/api/mp/cscene/monitor/flow/page";


}
