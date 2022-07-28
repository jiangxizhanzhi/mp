package cn.cyberpeace.mp.devops.cluster.constant;

/**
 * 控制层常量池
 * @author wanggang
 */
public final class ControllerConstant {

    private ControllerConstant(){
        throw new IllegalStateException("Utility class");
    }

    public static final String BASE_API_PATH = "/devops/cluster";

    public static final String ERROR_CODE_PREFIX = "devops.error_code.";


    public static final int MP_COMMON_DROPDOWN_TYPE_DEVOPS_CLUSTER_POSITION = 6;
    public static final int MP_COMMON_DROPDOWN_TYPE_DEVOPS_CLUSTER_USE = 7;

}
