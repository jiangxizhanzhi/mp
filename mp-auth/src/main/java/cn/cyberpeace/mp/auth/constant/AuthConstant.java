package cn.cyberpeace.mp.auth.constant;

/**
 * @Description: 全局变量
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class AuthConstant {

    public static final String ROOT_USER_ID = "3f655d87112f4ae094bbaf70830e20aa";

    public static final String PASSWORD_SECRET = "99bdfe0886604c49be29c977635572d5";

    public static final String PASSWORD_DES_KEY = "B13H089Y";

    public static final String JWT_SECRET = PASSWORD_SECRET;

    public static final String JWT_SECRET_KEY = JWT_SECRET;

    public static final String JWT_PREFIX = "JWT ";

    public static final String JWT_HEADER_KEY = "Authorization";

    public static final String ERROR_CODE_PREFIX = "auth.error_code.";

    public static final String TENANT_COLUMN_ID = "tenant";

    public static final String DEFAULT_TENANT = "default";

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String ORG_PATH_SEPARATOR = "/";

    public static final String USER_DEFAULT_IMG = "/page/mg/admin/43119f4d3c86e7b22045df3d72f3c116.png";

    private AuthConstant() {
    }
}
