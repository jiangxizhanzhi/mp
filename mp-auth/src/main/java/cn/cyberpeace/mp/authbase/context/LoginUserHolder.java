package cn.cyberpeace.mp.authbase.context;

/**
 * @ClassName LoginUserHolder
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/13 18:49
 * @Version 1.0.0
 */
public class LoginUserHolder {

    private static ThreadLocal<String> userContext = new ThreadLocal();

    /**
     * get
     *
     * @return
     */
    public static ThreadLocal<String> get() {
        return userContext;
    }


}
