package cn.cyberpeace.mp.auth.base.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/13 17:59
 */
@Component
public class LoginUserContext implements TransferContext<String> {

    private static String KEY = "USER_ID";

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
