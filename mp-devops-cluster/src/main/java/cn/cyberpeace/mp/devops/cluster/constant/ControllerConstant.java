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

    public static final String REAL_EQUIPMENT_USE_STATUS_PREFIX = "devops.cluster.real.equipment.use.status";
    public static final String REAL_EQUIPMENT_POSITION_STATUS_PREFIX = "devops.cluster.real.equipment.position.status";

}
