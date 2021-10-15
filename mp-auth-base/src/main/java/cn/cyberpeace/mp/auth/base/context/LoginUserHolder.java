package cn.cyberpeace.mp.auth.base.context;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/13 18:49
 */
public class LoginUserHolder {

    private static ThreadLocal<String> userContext = new ThreadLocal();

    public static ThreadLocal<String> get() {
        return userContext;
    }


}
