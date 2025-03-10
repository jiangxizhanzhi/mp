package cn.cyberpeace.mp.cscene.service.multirange.testrange;

public interface TestRangeApiUrl {
    //登录认证
    public static final String AUTH_TOKEN = "/api/mp/auth/login";

    public static final String BASIC_ELEMENT_MONITOR_INFO = "/api/mp/cscene/monitor/scene/all_elements/%s";

    public static final String AUTH_HEALTH_CHECK = "/api/mp/auth/user_permissions/menuidents/byloginuser";

    public static final String MAP_SCENE_ID = "/api/mp/cscene/cscenes/get_scene_id_by_vpn_server_ip";
}
