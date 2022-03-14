package cn.cyberpeace.mp.sceneplugincloud.constant;

public class CloudUrlConstant {
    public static final String CONTAINER_CREATE_URL = "/v1/scenarios/container";
    public static final String CONTAINER_DELETE_URL = "/v1/scenarios/container";
    public static final String CONTAINER_GET_URL = "/v1/scenarios/container/%s";
    public static final String CONTAINER_COPY_FILE_URL = "/v1/containers/%s/archive";
    public static final String CONTAINER_EXECUTE_COMMAND_URL = "/v1/containers/%s/execute";

    public static final String ENTITY_NODE_CREATE_URL = "/v1/scenarios/entity_node";
    public static final String ENTITY_NODE_DELETE_URL = "/v1/scenarios/entity_node";

    public static final String VIRTUAL_FIREWALL_CREATE_URL = "/v1/scenarios/virtual_firewall";
    public static final String VIRTUAL_FIREWALL_DELETE_URL = "/v1/scenarios/virtual_firewall";

    public static final String VIRTUAL_ROUTER_CREATE_URL = "/v1/scenarios/virtual_router";
    public static final String VIRTUAL_ROUTER_DELETE_URL = "/v1/scenarios/virtual_router";

    public static final String VIRTUAL_SWITCH_CREATE_URL = "/v1/scenarios/virtual_switch";
    public static final String VIRTUAL_SWITCH_DELETE_URL = "/v1/scenarios/virtual_switch";

    public static final String VIRTUAL_MACHINE_CREATE_URL = "/v1/scenarios/vm";
    public static final String VIRTUAL_MACHINE_DELETE_URL = "/v1/scenarios/vm";

    public static final String VIRTUAL_MACHINE_LIFE_CONTROL_URL = "/v1/instances/{instance_id}/action";
    public static final String CONTAINER_LIFE_CONTROL_URL = "/v1/containers/{instance_id}/action";

    public static final String VIRTUAL_INTERNET_CREATE_URL = "/v1/scenarios/virtual_internet";
    public static final String NETWORK_QUERY_URL = "/v1/networks";
    public static final String NETWORK_GET_URL = "/v1/networks/%s";

    public static final String CONSOLE_INFO_GET_URL = "/cpcloud/instance_control/v1/console";

    public static final String CREATE_CONNECTION_URL = "/cpcloud/guacamole/v1/connection";
    public static final String GET_CONNECTION_URL = "/cpcloud/guacamole/v1/connection";
    public static final String GET_MONITOR_URL = "/cpcloud/guacamole/v1/monitor";
    public static final String GET_ASSISTANCE_URL = "/cpcloud/guacamole/v1/assistance";
    public static final String GET_RECORDING_URL = "/cpcloud/guacamole/v1/recordfile";
    public static final String RECORDING_DOWNLOAD_URL = "/cpcloud/guacamole/v1/downloadfile?recordfilename=%s";
    public static final String ENABLE_RECORDING_URL = "/cpcloud/guacamole/v1/enable_record";
    public static final String DISABLE_RECORDING_URL = "/cpcloud/guacamole/v1/disable_record";
    public static final String DELETE_RECORDING_URL = "/cpcloud/guacamole/v1/filemanager";
    public static final String GET_RECORDING_NAME_URL = "/cpcloud/guacamole/v1/recordfile";

    public static final String CREATE_NPC_URL = "/knapsack/npc_node/v1/node";
    public static final String DELETE_NPC_URL = "/knapsack/npc_node/v1/node";

    private CloudUrlConstant() {}
}
