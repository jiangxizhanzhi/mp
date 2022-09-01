package cn.cyberpeace.mp.collection.core.constant;

/**
 * 缓存key
 */
public class CacheKeyConstant {
    private CacheKeyConstant() {

    }

    public static final String CACHE_KEY_FILE_EVENTS = "file_events";

    public static final String CACHE_KEY_CUSTOM_PROBES = "custom_probes";

    public static final String CACHE_KEY_VUL_INFOS = "vul_infos";

    public static final String CACHE_KEY_VUL_COUNTS = "vul_counts";

    public static final String CACHE_KEY_HOST_STATE = "host_states";

    public static final String CACHE_KEY_NET_CONN = "net_conn";

    public static final String CACHE_KEY_PROCESS = "process";

    public static final String CACHE_KEY_USERS = "users";

    public static final String CACHE_KEY_ENV_VAR_CHANGED_COUNT = "env_variable_changed_count";

    public static final String CACHE_KEY_SYSTEM_USERS_CHANGED_COUNT = "sys_users_changed_count";

    public static final String CACHE_KEY_PROCESS_CHANGED_COUNT = "processes_changed_count";

    public static final String CACHE_KEY_NET_CONNS_CHANGED_COUNT = "net_conns_changed_count";

    public static final String CACHE_KEY_AGGS_BY_AGENTNAME = "aggs_by_agentname";

    public static final String CACHE_KEY_AGGS_RULEIDS_BY_AGENTNAME = "aggs_ruleids_by_agentname";

    public static final String CACHE_KEY_COUNT_BY_AGENTNAME = "count_by_agentname";
}
