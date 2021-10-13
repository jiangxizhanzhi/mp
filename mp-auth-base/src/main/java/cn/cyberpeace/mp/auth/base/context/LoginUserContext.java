package cn.cyberpeace.mp.auth.base.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/13 17:59
 */
public class LoginUserContext implements TransferContext<String> {

    private static String KEY = "LOGIN_USER_ID";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return LoginUserHolder.get().get();
    }

    @Override
    public void setValue(String value) {
        LoginUserHolder.get().set(value);
    }
}
