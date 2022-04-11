package cn.cyberpeace.mp.situationaware.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
  *@Desciption 
  *@Author zhangjin
  *@Date 9/3/21 10:22 上午
**/
public enum SituationalAwareErrorCode implements ErrorCode {
    CONNECTION_FAIL("MP-110400", "connection_fail"),
    PROBE_DELETE_FAIL("MP-110401", "probe_delete_fail"),
    PROBE_MODIFY_FAIL("MP-110402", "probe_update_fail"),
    PROBE_MODIFY_PARAM_ERROR("MP-110403", "probe_update_param_error"),
    PROBE_MANAGER_ADD_ERROR("MP-110404", "probe_manager_add_script_error"),
    PROBE_MANAGER_BIND_ERROR("MP-110417", "probe_bind_agents_error"),
    PROBE_NAME_EXIST("MP-110405", "probe_name_already_exit"),
    PROBE_MANAGER_ES_QUERY_ERROR("MP-110406", "probe_es_query_error"),
    PROBE_MANAGER_DELETE_PARAM_ERROR("MP-110407", "probe_delete_param_is_empty"),
    ARKIME_ADD_ERROR("MP-110408", "arkime_instance_add_error"),
    ARKIME_DELETE_ERROR("MP-110409", "delete_arkime_instance_error"),
    ADD_FLOWTABLE_ERROR("MP-110410", "add_flow_table_error"),
    DELETE_FLOWTABLE_ERROR("MP-110411", "delete_flow_table_error"),
    BINDING_MIRROR_ERROR("MP-110412", "binding_flow_mirror_error"),
    WAZUH_AGENTS_QUERY_ERROR("MP-110413", "wazuh_agents_query_error"),
    WAZUH_AGENTS_QUERY_PORT_ERROR("MP-110414", "wazuh_ports_query_error"),
    WAZUH_AGENTS_QUERY_PROCESS_ERROR("MP-110415", "wazuh_agent_process_query_error"),
    WAZUH_AGENTS_QUERY_EVENT_ERROR("MP-110416", "wazuh_agent_query_event_error"),
    WAZUH_AGENTS_QUERY_HARDWARE_ERROR("MP-110417", "wazuh_agent_hardware_query_error"),

    WAZUH_AUTHENTICATION_FAIL("MP-110420","wazuh_authentication_fail");

    SituationalAwareErrorCode(String code, String message) {
        setCode(code, SituationalAwareConstant.ERROR_CODE_PREFIX + message);
    }
}
