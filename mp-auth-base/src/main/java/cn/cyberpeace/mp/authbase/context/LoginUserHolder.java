package cn.cyberpeace.mp.authbase.context;


public class LoginUserHolder {

    private static ThreadLocal<String> userContext = new ThreadLocal();

    public static ThreadLocal<String> get() {
        return userContext;
    }


}
