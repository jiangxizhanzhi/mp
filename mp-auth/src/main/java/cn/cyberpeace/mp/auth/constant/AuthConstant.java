package cn.cyberpeace.mp.auth.constant;

/**
 * @Description: 全局变量
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class AuthConstant {

    public static final String ROOT_USER_ID = "3f655d87112f4ae094bbaf70830e20aa";

    public static final String ROOT_ORG_ID = "a13262d600fe40c799d63dce5c1485af";

    public static final String PASSWORD_SECRET = "99bdfe0886604c49be29c977635572d5";

    public static final String PASSWORD_DES_KEY = "B13H089Y";

    public static final String JWT_SECRET = PASSWORD_SECRET;

    public static final String JWT_SECRET_KEY = JWT_SECRET;

    public static final String JWT_PREFIX = "JWT ";

    public static final String JWT_HEADER_KEY = "Authorization";

    public static final String CP_TOKEN_REFRESHED_HEADER_KEY = "CP-Token-Refreshed";

    public static final String ERROR_CODE_PREFIX = "auth.error_code.";

    public static final String TENANT_COLUMN_ID = "tenant";

    public static final String DEFAULT_TENANT = "default";

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String ROLE_ADMIN_ID = "5827ff7847504ffd806e0a85c417228d";


    public static final String ORG_PATH_SEPARATOR = "/";

    public static final String FDFS_PREFIX = "/fdfs";

    public static final String DEFAULT_IMAGE_PREFIX = "/api/mp";

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    public static final Integer TARGET_FIELD_VALUE_PK_0 = 0;
    public static final Integer TARGET_FIELD_VALUE_VISIBLE_1 = 1;

    public static final Integer USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0 = 0;

    public static final String VERIFY_JWT_TOKEN_ERROR = "Verify jwt token error: {}";
    public static final String DECODE_JWT_TOKEN_ERROR = "Decode jwt token {} error: {}";


    private AuthConstant() {
    }
}
