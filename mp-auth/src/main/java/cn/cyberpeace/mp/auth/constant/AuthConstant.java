package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;

import java.util.Arrays;
import java.util.List;


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

    
    public static final String COMMA = ",";

    public static final Integer TARGET_FIELD_VALUE_PK_0 = 0;
    public static final Integer TARGET_FIELD_VALUE_VISIBLE_1 = 1;

    public static final Integer USER_ONLINE_RECORD_FIELD_VALUE_STATUS_0 = 0;

    public static final String FAIL_VERIFY_JWT_TOKEN = "failed to verify jwt token, massage: userId={}, token={}";
    public static final String FAIL_DECODE_JWT_TOKEN = "failed to decode jwt token, massage: token={}, error={}";
    public static final String FAIL_CREATE_JWT_TOKEN = "Create jwt token error: {}";

    public static final String JWT_ALGORITHM = "HmacSHA256";
    public static final String JWT_CLAIM_ID = "id";
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String JWT_CLAIM_KEY = "key";

    public static final long TOKEN_EXPIRE_TIME = 604800L;
    public static final int MILLI_TO_SECOND = 1000;

    public static final String SEPARATOR_CHAR_DOT = ".";
    public static final String JOIN_TYPES_DELIMITER = ",";

    
    public static final long UPLOAD_AVATAR_MAX_SIZE = 20 * 1024 * 1024L;

    
    public static final List<String> UPLOAD_AVATAR_SUFFIX_TYPE = Arrays.asList("png", "jpeg", "jpg");

    
    public static final List<String> UPLOAD_AVATAR_MAGIC_HEADER_TYPE = Arrays.asList("png", "jpg");


    
    public static final String AUTH_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".auth";

    public static final String ROUTING_KEY_USER_DELETE = AUTH_EXCHANGE_NAME + ".user.delete";
    public static final String ROUTING_KEY_USER_MODIFY = AUTH_EXCHANGE_NAME + ".user.modify";
    public static final String ROUTING_KEY_USER_CREATE = AUTH_EXCHANGE_NAME + ".user.create";

    public static final String ROUTING_KEY_ORG_DELETE = AUTH_EXCHANGE_NAME + ".org.delete";
    public static final String ROUTING_KEY_ORG_MODIFY = AUTH_EXCHANGE_NAME + ".org.modify";
    public static final String ROUTING_KEY_ORG_CREATE = AUTH_EXCHANGE_NAME + ".org.create";

    public static final String ROUTING_KEY_ROLE_DELETE = AUTH_EXCHANGE_NAME + ".role.delete";
    public static final String ROUTING_KEY_ROLE_MODIFY = AUTH_EXCHANGE_NAME + ".role.modify";
    public static final String ROUTING_KEY_ROLE_CREATE = AUTH_EXCHANGE_NAME + ".role.create";

    public static final String MSG_TYPE_USER_DELETE = "user_delete";
    public static final String MSG_TYPE_USER_MODIFY = "user_modify";
    public static final String MSG_TYPE_ORG_DELETE = "org_delete";
    public static final String MSG_TYPE_ROLE_DELETE = "role_delete";
    public static final String MSG_TYPE_ROLE_MODIFY = "role_modify";

    public static final String KAPTCHA_SESSION_KEY = "kaptchaKey";
    public static final String KAPTCHA_SESSION_DATE = "kaptchaDate";

    public static final Long VERIFY_CODE_LIFE_TIME = 5 * 60 * 1000L;

    public static final String REQUEST_PARAMETER_VERIFY_CODE = "verify_code";
    public static final String REQUEST_PARAMETER_USERNAME = "username";

    public static final String PASSWORD_ERROR_COUNT_KEY = "password_error_count";
    public static final String LOGIN_NEED_VERIFY_CODE = "login_need_verify_code";

    private AuthConstant() {
    }
}
