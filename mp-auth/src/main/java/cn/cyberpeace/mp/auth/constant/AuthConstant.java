package cn.cyberpeace.mp.auth.constant;

public class AuthConstant {

    public static final String ROOT_USER_ID = "3f655d87112f4ae094bbaf70830e20aa";

    public static final String SUPERUSER_ROLE_ID = "e12bb08a9bc545bbba46a2fb3c92533a";

    public static final String ADMIN_ROLE_ID = "5827ff7847504ffd806e0a85c417228d";

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

    public static final String ROLE_SUPERUSER = "SUPERUSER";

    public static final String ORG_PATH_SEPARATOR = "/";

//    public static final String AUTHORITY_USER_MANAGE = "user-manage";

//    public static final String AUTHORITY_ROLE_MANAGE = "role-management";

    private AuthConstant() {}
}
