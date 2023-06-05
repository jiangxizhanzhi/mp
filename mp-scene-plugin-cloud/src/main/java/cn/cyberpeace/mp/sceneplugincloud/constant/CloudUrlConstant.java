package cn.cyberpeace.mp.sceneplugincloud.constant;

public class CloudUrlConstant {
    public static final String MACVLAN_CONTAINER_CREATE_URL = "/v1/scenarios/macvlan_container";
    public static final String CONTAINER_CREATE_URL = "/v1/scenarios/container";
    public static final String CONTAINER_DELETE_URL = "/v1/scenarios/container";
    public static final String CONTAINER_GET_URL = "/v1/scenarios/container/%s";
    public static final String CONTAINER_COPY_FILE_URL = "/v1/containers/%s/archive";
    public static final String CONTAINER_EXECUTE_COMMAND_URL = "/v1/containers/%s/execute";

    public static final String ENTITY_NODE_CREATE_URL = "/v1/scenarios/entity_node";
    public static final String ENTITY_NODE_DELETE_URL = "/v1/scenarios/entity_node";

    public static final String VIRTUAL_FIREWALL_CREATE_URL = "/v1/scenarios/virtual_firewall";
    public static final String VIRTUAL_FIREWALL_UPDATE_URL = "/v1/scenarios/update_virtual_firewall_rule";
    public static final String VIRTUAL_FIREWALL_DELETE_URL = "/v1/scenarios/virtual_firewall";

    public static final String VIRTUAL_ROUTER_CREATE_URL = "/v1/scenarios/virtual_router";
    public static final String VIRTUAL_ROUTER_UPDATE_URL = "/v1/routers/{router_id}";
    public static final String VIRTUAL_ROUTER_DELETE_URL = "/v1/scenarios/virtual_router";

    public static final String VIRTUAL_SWITCH_CREATE_URL = "/v1/scenarios/virtual_switch";
    public static final String VIRTUAL_SWITCH_UPDATE_URL = "/v1/networks/{network_id}";
    public static final String VIRTUAL_SWITCH_DELETE_URL = "/v1/scenarios/virtual_switch";

    public static final String VIRTUAL_MACHINE_CREATE_URL = "/v1/scenarios/vm";
    public static final String VIRTUAL_MACHINE_DELETE_URL = "/v1/scenarios/vm";

    public static final String VIRTUAL_MACHINE_SNAPSHOT_URL = "/v1/scenarios/node_snapshot";
    public static final String VIRTUAL_MACHINE_DEL_SNAPSHOT_URL = "/v1/scenarios/device_snapshot";

    public static final String VIRTUAL_MACHINE_LIFE_CONTROL_URL = "/v1/instances/{instance_id}/action";
    public static final String CONTAINER_LIFE_CONTROL_URL = "/v1/containers/{instance_id}/action";

    public static final String VIRTUAL_INTERNET_CREATE_URL = "/v1/scenarios/virtual_internet";
    public static final String NETWORK_QUERY_URL = "/v1/networks";
    public static final String NETWORK_GET_URL = "/v1/networks/%s";

    public static final String CONSOLE_INFO_GET_URL = "/cpcloud/instance_control/v1/console";
    public static final String COM_INFO_GET_URL = "/cpcloud/instance_control/v1/telnet";

    public static final String CREATE_CONNECTION_URL = "/cpcloud/guacamole/v1/connection";
    public static final String EXPIRE_ALL_CONNECT = "/cpcloud/guacamole/v1/logout";
    public static final String GET_CONNECTION_URL = "/cpcloud/guacamole/v1/connection";
    public static final String GET_MONITOR_URL = "/cpcloud/guacamole/v1/monitor";
    public static final String GET_ASSISTANCE_URL = "/cpcloud/guacamole/v1/assistance";
    public static final String GET_RECORDING_URL = "/cpcloud/guacamole/v1/recordfile";
    public static final String RECORDING_DOWNLOAD_URL = "/cpcloud/guacamole/v1/downloadfile?recordfilename=%s";
    public static final String RECORDING_PLAY_URL = "/cpcloud/guacamole/v1/video?recordfilename=%s";
    public static final String ENABLE_RECORDING_URL = "/cpcloud/guacamole/v1/enable_record";
    public static final String DISABLE_RECORDING_URL = "/cpcloud/guacamole/v1/disable_record";
    public static final String CONVERT_RECORDING_URL = "/cpcloud/guacamole/v1/convert_record";
    public static final String DELETE_RECORDING_URL = "/cpcloud/guacamole/v1/filemanager";
    public static final String GET_RECORDING_NAME_URL = "/cpcloud/guacamole/v1/recordfile";
    public static final String GET_RECORD_CLEAN_CONFIG = "/cpcloud/guacamole/v1/record_clean_config";
    public static final String UPDATE_RECORD_CLEAN_CONFIG = "/cpcloud/guacamole/v1/record_clean_config";

    public static final String CREATE_NPC_URL = "/cpcloud/npc_node/v1/node";
    public static final String DELETE_NPC_URL = "/cpcloud/npc_node/v1/node";
    public static final String GET_NPC_URL = "/cpcloud/npc_node/v1/node";

    public static final String GET_ALL_CONTAINER_URL = "/v1/containers";
    public static final String DELETE_CONTAINER_URL = "/v1/containers/{container_id}";
    public static final String GET_ALL_INSTANCE_URL = "/v1/instances";
    public static final String DELETE_INSTANCE_URL = "/v1/instances/{instance_id}";
    public static final String GET_INSTANCE_URL = "/v1/instances/{instance_id}";
    public static final String DELETE_NETWORK_URL = "/v1/networks/{network_id}";
    public static final String GET_ALL_ROUTER_URL = "/v1/routers";
    public static final String DELETE_ROUTER_URL = "/v1/routers/{router_id}";
    public static final String QUERY_PORTS = "/v1/ports/{resource_id}";
    public static final String ADD_VM_PORTS = "/v1/instances/{instance_id}/interfaces";
    public static final String DELETE_VM_PORTS = "/v1/instances/{instance_id}/interfaces/{port_id}";

    public static final String CREATE_PORTS = "/v1/ports";
    public static final String UPDATE_PORTS = "/v1/ports";

    public static final String MODIFY_INSTANCE_IP = "/v1/scenarios/modify_instance_ip";

    public static final String GET_SERIAL_PORT_URL = "/v1/instances/{instance_id}/serial-port";
    public static final String ROUTER_CMD_SEND_URL = "/cpcloud/router/v1/send_rule";
    public static final String ROUTER_CONFIG_QUERY_URL = "/cpcloud/router/v1/get_rule";

    public static final String VLAN_RANGE = "/cpcloud/info/v1/vlan_range";
    public static final String DOWNLOAD_CONTAINER_FILE = "/cpcloud/info/v1/download_pcap";
    public static final String CONTAINER_ADD_ROUTE = "/cpcloud/info/v1/container_add_route";

    public static final String LINK_STATUS = "/cpcloud/instance_control/v1/link_status";
    public static final String SCP_FILE = "/cpcloud/instance_control/v1/scpfile";
    public static final String SSH_CMD = "/cpcloud/instance_control/v1/ssh_cmd";

    public static final String ENTITY_NODE_MIB_UPLOAD_URL = "/cpcloud/instance_control/v1/upload_mib_file";

    public static final String ENTITY_NODE_MIB_NODE_INFO_URL = "/cpcloud/instance_control/v1/get_mib_node_info";
    public static final String ATTACH_ISO = "/cpcloud/device/v1/iso/vm_attach";
    public static final String ATTACH_VIRTUAL_USBS = "/cpcloud/device/v1/virtual_usbs/vm_attach";
    public static final String DELETE_VIRTUAL_USBS = "/cpcloud/device/v1/virtual_usbs/vm_attach";
    public static final String ATTACH_ENTITY_USBS = "/cpcloud/device/v1/entity_usbs/vm_attach";
    public static final String DELETE_ENTITY_USBS = "/cpcloud/device/v1/entity_usbs/vm_attach";

    public static final String DEVICE_SNAPSHOT = "/cpcloud/device/v1/snapshot";

    public static final String FORCE_DELETE_VM = "/cpcloud/host/v1/vm";
    public static final String FORCE_DELETE_CONTAINER = "/cpcloud/host/v1/container";
    public static final String FORCE_DELETE_NETWORK = "/cpcloud/host/v1/network";
    public static final String FORCE_DELETE_ROUTER = "/cpcloud/host/v1/router";

    public static final String SNAPSHOT_LIST = "/cpcloud/device/v1/snapshot";
    public static final String GET_CPCLOUD_ALL_ORIGIN_IPS = "/cpcloud/info/v1/revise_ip";
    public static final String RESET_CPCLOUD_ALL_IPS = "/cpcloud/info/v1/revise_ip";
    public static final String NETWORK_FLOW_RATE = "/cpcloud/info/v1/network_flow_rate";

    public static final String CREATE_FLAVOR = "/v1/flavors";

    public static final String CREATE_DYNAMIPS = "/cpcloud/device/v1/dynamips/vm";
    public static final String DELETE_DYNAMIPS = "/cpcloud/device/v1/dynamips/vm";
    public static final String LIST_DYNAMIPS = "/cpcloud/device/v1/dynamips/instances";
    public static final String STATISTICS_DYNAMIPS = "/cpcloud/device/v1/dynamips/statistics";
    public static final String DYNAMIPS_SEND_RULE = "/cpcloud/device/v1/dynamips/instances/{instance_id}/send_rule";
    public static final String VIRTUAL_DYNAMIPS_LIFE_CONTROL_URL = "/cpcloud/device/v1/dynamips/{instance_id}/action";
    public static final String GET_DYNAMIPS_INFO = "/cpcloud/device/v1/dynamips/type";

    private CloudUrlConstant() {
    }
}
