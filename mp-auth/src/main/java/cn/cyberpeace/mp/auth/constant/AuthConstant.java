package cn.cyberpeace.mp.auth.constant;

public class AuthConstant {
    public static final String PASSWORD_SECRET = "99bdfe0886604c49be29c977635572d5";

    public static final String JWT_SECRET = PASSWORD_SECRET;

    public static final String JWT_SECRET_KEY = JWT_SECRET;

    public static final String JWT_PREFIX = "JWT ";

    public static final String JWT_HEADER_KEY = "Authorization";

    private AuthConstant() {}
}
