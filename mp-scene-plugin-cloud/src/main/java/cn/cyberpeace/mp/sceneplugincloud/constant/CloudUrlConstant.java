 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudUrlConstant.java.
 // File size 14968.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.constant;

/**
 * @ClassName CloudUrlConstant
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CloudUrlConstant {
    /**
     * macvlan, 30s版本没有
     */
    public static final String MACVLAN_CONTAINER_CREATE_URL = "/cloud/v1/scenarios/macvlan_container";
    /**
     * 修改节点接口，可以不用
     */
    public static final String CONTAINER_RESIZE_URL = "/cloud/v1/containers/{container_id}";
    /**
     * 修改节点接口，可以不用
     */
    public static final String CONTAINER_ADD_PORT_URL = "/cloud/v1/scenarios/attach_network";
    /**
     * 修改节点接口，可以不用
     */
    public static final String CONTAINER_DELETE_PORT_URL = "/cloud/v1/scenarios/detach_network";

    /**
     *
     * todo
     * 从容器中复制文件接口，目前cloud接口没有。
     * 主要用于给容器按照unzip以及获取容器中文件。
     * 会影响到数据采集和流量。
     *
     * 1. 会影响到容器解压安装wazuh
     */
    public static final String CONTAINER_COPY_FILE_URL = "/cloud/v1/container/%s/copy_to_container";

    /**
     * 容器获取接入url接口，
     */
    public static final String CONTAINER_GET_URL = "/cloud/v1/scenarios/container/%s";

    /**********************************************************************************************/


    /*******************容器接口******************/
    public static final String GET_ALL_CONTAINER_URL = "/cloud/v1/container";

    public static final String DELETE_CONTAINER_URL = "/cloud/v1/container/{container_id}";

    public static final String GET_CONTAINER_URL = "/cloud/v1/container/{container_id}";


    /**
     * 容器创建接口，这个是必须的
     */
    public static final String CONTAINER_CREATE_URL = "/cloud/v1/scenarios/container";
    /**
     * 容器删除接口，这个也是必须的。
     */
    public static final String CONTAINER_BATCH_DELETE_URL = "/cloud/v1/scenarios/container";

    /**
     * 获取容器console 连接
     */
    public static final String CONTAINER_ACTION_URL = "/cloud/v1/container/{container_id}/{action}";

    /**
     * 容器执行命令接口
     */
    public static final String CONTAINER_EXECUTE_COMMAND_URL = "/cloud/v1/container/%s/exec";

    public static final String CONTAINER_LIFE_CONTROL_URL = "/cloud/v1/container/{container_id}/{action}";
    public static final String CONTAINER_SNAPSHOT_URL = "/cloud/v1/container/{container_id}/snapshot";
    public static final String CONTAINER_DELETE_SNAPSHOT_URL = "/cloud/v1/container/{container_id}/snapshot/{image_id}";
    public static final String CONTAINER_SNAPSHOT_ACTION_URL = "/cloud/v1/container/{container_id}/snapshot/{image_id}/{action}";



    /*******************实体设备接口*****************/

    public static final String ENTITY_NODE_CREATE_URL = "/cloud/v1/physical/physical_port";
    public static final String ENTITY_NODE_DELETE_URL = "/cloud/v1/physical/physical_port";

    public static final String LOGICAL_SWITCH_CREATE_URL = "/cloud/v1/physical/logical_switch";
    public static final String LOGICAL_SWITCH_DELETE_URL = "/cloud/v1/physical/logical_switch";


    /****************firewall**********************/
    public static final String VIRTUAL_FIREWALL_CREATE_URL = "/cloud/v1/scenarios/firewall";
    public static final String VIRTUAL_FIREWALL_DELETE_URL = "/cloud/v1/scenarios/firewall";
    public static final String VIRTUAL_FIREWALL_POLICY_CREATE_URL = "/cloud/v1/scenarios/firewall_policy";
    public static final String VIRTUAL_FIREWALL_UPDATE_URL = "/cloud/v1/scenarios/firewall_policy";


    /*********************dynamips*********************/

    public static final String DYNAMIPS_CREATE_URL = "/cloud/v1/scenarios/dynamips";
    public static final String DYNAMIPS_DELETE_URL = "/cloud/v1/scenarios/dynamips";
    public static final String DYNAMIPS_GET_URL = "/cloud/v1/scenarios/dynamips";
    public static final String DYNAMIPS_IMAGE_GET_URL = "/cloud/v1/scenarios/dynamips/images";


    /**************************router****************************/
    public static final String VIRTUAL_ROUTER_CREATE_URL = "/cloud/v1/scenarios/virtual_router";
    public static final String VIRTUAL_ROUTER_DELETE_URL = "/cloud/v1/scenarios/virtual_router";
    public static final String VIRTUAL_ROUTER_MODIFY_URL = "/cloud/v1/routers/{router_id}";
    //这个用来做虚实结合，多靶场的
    public static final String VIRTUAL_ROUTER_BIND_NETWORK_URL = "/cloud/v1/router/{router_id}/network";
    public static final String VIRTUAL_ROUTER_UNBIND_NETWORK_URL = "/cloud/v1/router/{router_id}/network";

    /*****************网络*********************/
    public static final String NETWORK_CREATE_URL = "/cloud/v1/network";
    public static final String NETWORK_DELETE_URL = "/cloud/v1/network/{network_id}";
    public static final String NETWORK_ACL_CREATE_URL = "/cloud/v1/network/{network_id}/acl";
    public static final String NETWORK_ACL_DELETE_URL = "/cloud/v1/network/{network_id}/acl";
    public static final String NETWORK_GET_URL = "/cloud/v1/network/%s";
    public static final String GET_EXTERNAL_NETWORK = "/cloud/v1/scenarios/virtual_internet";
    public static final String NETWORK_QUERY_URL = "/cloud/v1/network";

    /******************模板*****************/
    public static final String GET_TEMPLATE_ID_BY_DEVICE_NAME = "/cloud/v1/device/template_by_device?name=%s";



    /***********************虚机**********************/
    public static final String DELETE_INSTANCE_URL = "/cloud/v1/vm/{vm_id}";

    public static final String VIRTUAL_MACHINE_ACCESS_URL = "/cloud/v1/vm/console";
    public static final String VIRTUAL_MACHINE_CREATE_URL = "/cloud/v1/scenarios/vm";
    public static final String VIRTUAL_MACHINE_DELETE_URL = "/cloud/v1/scenarios/vm";
    public static final String GET_VM_LIST_URL = "/cloud/v1/vm";
    public static final String VIRTUAL_MACHINE_SNAPSHOT_URL = "/cloud/v1/vm/{vm_id}/snapshot";
    public static final String VIRTUAL_MACHINE_DELETE_SNAPSHOT_URL = "/cloud/v1/vm/{vm_id}/snapshot/{snapshot_id}";
    public static final String VIRTUAL_MACHINE_SNAPSHOT_LIST = "/cloud/v1/vm/{vm_id}/snapshot";
    public static final String VIRTUAL_MACHINE_SNAPSHOT_ACTION_URL = "/cloud/v1/vm/{vm_id}/snapshot/{snapshot_id}/{action}";
    public static final String VIRTUAL_MACHINE_LIFE_CONTROL_URL = "/cloud/v1/vm/{vm_id}/{action}";
    public static final String VIRTUAL_MACHINE_EXEC_CMD = "/cloud/v1/vm/exec";
    public static final String VIRTUAL_MACHINE_COM_EXEC_CMD = "/cloud/v1/vm/{vm_id}/com_exec";
    public static final String VIRTUAL_MACHINE_MODIFY_URL = "/cloud/v1/vm/{vm_id}";

    public static final String GUACAMOLE_CONNECTION_CREATE = "/guacamole/connection";
    public static final String GUACAMOLE_CONNECTION_DELETE = "/guacamole/connection";

    /*****************获取模板id******************/
    public static final String VIRTUAL_DEVICE_GET = "/cloud/v1/device/template_by_device";
    public static final String VIRTUAL_INTERNET_CREATE_URL = "/cloud/v1/scenarios/virtual_internet";

    /************connect*************************/
    public static final String COM_INFO_GET_URL = "/cloud/v1/vm/{vm_id}/com";
    public static final String CREATE_CONNECTION_URL = "/cloud/v1/guacamole/connection";
    public static final String DELETE_CONNECTION_URL = "/cloud/v1/guacamole/connection";
    public static final String QOS_CREATE_URL = "/cloud/v1/qos";
    public static final String INSTANCE_MODIFY_NETWORK = "/cloud/v1/scenarios/node/network";


    /********************physic_switch***************/
    public static final String GET_PHYSICAL_SWITCH_PORT_CONNECTION = "/cloud/v1/physical/l1_switch";
    public static final String GET_PHYSICAL_PORT_URL = "/cloud/v1/physical/l1_switch_ports";
    public static final String ADD_PHYSICAL_SWITCH_PORT_CONNECTION = "/cloud/v1/physical/l1_switch";
    public static final String DELETE_PHYSICAL_SWITCH_PORT_CONNECTION = "/cloud/v1/physical/l1_switch";

    /*****************mirror************************/
    public static final String CREATE_MIRROR_URL = "/cloud/v1/mirror";
    public static final String DELETE_MIRROR_URL = "/cloud/v1/mirror/{mirror_id}";














    /*****************************************************************************************************/



    public static final String VIRTUAL_MACHINE_DEL_SNAPSHOT_URL = "/v1/scenarios/device_snapshot";

    public static final String VIRTUAL_MACHINE_RESIZE_URL = "/v1/scenarios/instance_resize";

    public static final String VIRTUAL_SWITCH_UPDATE_URL = "/v1/networks/{network_id}";






    public static final String CONSOLE_INFO_GET_URL = "/cpcloud/instance_control/v1/console";


    public static final String EXPIRE_ALL_CONNECT = "/cpcloud/guacamole/v1/logout";
    public static final String GET_CONNECTION_URL = "/cpcloud/guacamole/v1/connection";
    public static final String GET_MONITOR_URL = "/cpcloud/guacamole/v1/monitor";
    public static final String GET_ASSISTANCE_URL = "/cpcloud/guacamole/v1/assistance";
    public static final String GET_RECORDING_URL = "/cpcloud/guacamole/v1/recordfile";
    public static final String RECORDING_DOWNLOAD_URL = "/cpcloud/guacamole/v1/downloadfile?recordfilename=%s";
    public static final String RECORDING_PLAY_URL = "/cpcloud/guacamole/v1/video?recordfilename=%s";
    public static final String ENABLE_RECORDING_URL = "/cloud/v1/guacamole/enable_record";
    public static final String DISABLE_RECORDING_URL = "/cpcloud/guacamole/v1/disable_record";
    public static final String CONVERT_RECORDING_URL = "/cpcloud/guacamole/v1/convert_record";
    public static final String DELETE_RECORDING_URL = "/cpcloud/guacamole/v1/filemanager";
    public static final String GET_RECORDING_NAME_URL = "/cpcloud/guacamole/v1/recordfile";
    public static final String GET_RECORD_CLEAN_CONFIG = "/cpcloud/guacamole/v1/record_clean_config";
    public static final String UPDATE_RECORD_CLEAN_CONFIG = "/cpcloud/guacamole/v1/record_clean_config";
    public static final String ACTIVE_CONNECTION_URL = "/cpcloud/guacamole/v1/active_connection";

    public static final String CREATE_NPC_URL = "/cpcloud/npc_node/v1/node";
    public static final String DELETE_NPC_URL = "/cpcloud/npc_node/v1/node";
    public static final String GET_NPC_URL = "/cpcloud/npc_node/v1/node";

    public static final String GET_INSTANCE_URL = "/v1/instances/{instance_id}";
    public static final String DELETE_NETWORK_URL = "/v1/networks/{network_id}";
    public static final String GET_ALL_ROUTER_URL = "/v1/routers";
    public static final String DELETE_ROUTER_URL = "/v1/routers/{router_id}";
    public static final String QUERY_PORTS = "/v1/ports/{resource_id}";
    public static final String ADD_VM_PORTS = "/v1/instances/{instance_id}/interfaces";
    public static final String DELETE_VM_PORTS = "/v1/instances/{instance_id}/interfaces/{port_id}";
    public static final String CREATE_PORT_MAPPING = "/cpcloud/fwd_app/v1/";
    public static final String DELETE_PORT_MAPPING = "/cpcloud/fwd_app/v1/{fwd_id}";
    public static final String EXEC_HOST_COMMAND = "/cpcloud/info/v1/command";

    public static final String CREATE_PORTS = "/v1/ports";
    public static final String UPDATE_PORTS = "/v1/ports";
    public static final String DELETE_PORTS = "/v1/ports/{port_id}";

    public static final String MODIFY_INSTANCE_IP = "/v1/scenarios/modify_instance_ip";
    public static final String UPDATE_QOS_RULE = "/v1/scenarios/update_qos_rules";
    public static final String CREATE_QOS_RULE = "/v1/scenarios/create_qos_rules";

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

    public static final String CREATE_FLAVOR = "/v1/scenarios/flavor";

    public static final String CREATE_DYNAMIPS = "/cpcloud/device/v1/dynamips/vm";
    public static final String DELETE_DYNAMIPS = "/cpcloud/device/v1/dynamips/vm";
    public static final String LIST_DYNAMIPS = "/cpcloud/device/v1/dynamips/instances";
    public static final String STATISTICS_DYNAMIPS = "/cpcloud/device/v1/dynamips/statistics";
    public static final String DYNAMIPS_SEND_RULE = "/cpcloud/device/v1/dynamips/instances/{instance_id}/send_rule";
    public static final String VIRTUAL_DYNAMIPS_LIFE_CONTROL_URL = "/cpcloud/device/v1/dynamips/{instance_id}/action";
    public static final String GET_DYNAMIPS_INFO = "/cpcloud/device/v1/dynamips/type";

    public static final String HYPERVISORS_STATISTICS = "/v1/hypervisors";

    /**
     * CloudUrlConstant
     */
    private CloudUrlConstant() {
    }
}
