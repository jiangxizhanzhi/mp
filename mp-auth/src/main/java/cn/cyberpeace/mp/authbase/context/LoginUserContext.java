package cn.cyberpeace.mp.authbase.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoginUserContext
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/13 17:59
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class LoginUserContext implements TransferContext<String> {

    private static String KEY = "USER_ID";

    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return KEY;
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public String getValue() {
        return LoginUserHolder.get().get();
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(String value) {
        LoginUserHolder.get().set(value);
    }
}
