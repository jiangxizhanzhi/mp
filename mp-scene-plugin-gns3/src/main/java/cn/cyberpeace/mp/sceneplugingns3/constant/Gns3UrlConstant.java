package cn.cyberpeace.mp.sceneplugingns3.constant;

public class Gns3UrlConstant {
    public static String CREATE_PROJECT = "/v2/projects";
    public static String DELETE_PROJECT = "/v2/projects/{project_id}";
    public static String START_PROJECT = "/v2/projects/{project_id}/nodes/start";
    public static String STOP_PROJECT = "/v2/projects/{project_id}/nodes/stop";

    public static String CREATE_PROJECT_NODE = "/v2/projects/{project_id}/nodes";
    public static String DELETE_PROJECT_NODE = "/v2/projects/{project_id}/nodes/{node_id}";
    public static String START_PROJECT_NODE = "/v2/projects/{project_id}/nodes/{node_id}/start";
    public static String UPDATE_ROUTER_CONFIG = "/v2/projects/{project_id}/files/project-files/dynamips/{node_id}/configs/i{dynamips_id}_startup-config.cfg";

    public static String CREATE_PROJECT_LINK = "/v2/projects/{project_id}/links";

    public static String UPDATE_VPCS = "/v2/projects/{project_id}/nodes/{node_id}/files/startup.vpc";

    public static String EXECCMD = "/cpcloud/info/v1/command";
}
