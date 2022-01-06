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

    public static final String STR_PER_PAGE = "per_page";

    public static final String CODE = "0";

    public static final String ERROR_CODE_PREFIX = "devops.error_code.";

}
