package cn.cyberpeace.mp.auth.service.sys;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class LicenseService {

    public interface Awesome extends Library {
//        Awesome INSTANCE = (Awesome) Native.loadLibrary("/home/tmp/license.so", Awesome.class);
        Awesome INSTANCE = (Awesome) Native.loadLibrary("classpath:/license/license.so", Awesome.class);

        public class GoString extends Structure {
            public static class ByValue extends GoString implements Structure.ByValue {}
            public String p;
            public long n;

            protected List getFieldOrder(){
                return Arrays.asList(new String[]{"p","n"});
            }

        }

        public int Verify(GoString.ByValue js, GoString.ByValue code);
    }


    /**
     *
     * @param verifyStr 校验json串，如 "{\"machinecode\": \"CHTF-12H1-34S8-HV23-123S\", \"deadline\": 1, \"info\": {\"thj\":2}}"
     * @param verifyCode 校验码
     * @return
     */
    public boolean verify(String verifyStr, String verifyCode) {
        log.info("verifyStr:{}, verifyCode:{}", verifyStr, verifyCode);

        Awesome.GoString.ByValue str = new Awesome.GoString.ByValue();
        str.p = verifyStr;
        str.n = str.p.length();

        Awesome.GoString.ByValue code = new Awesome.GoString.ByValue();
        code.p = verifyCode;
        code.n = code.p.length();

        int ret = Awesome.INSTANCE.Verify(str, code);

        //  -1	json传入错误
        //  0	此授权码正确
        //  1	此授权码错误
        if (ret == -1) {
            throw new MpRuntimeException("json传入错误！");
        }
        if (ret == 1) {
            throw new MpRuntimeException("授权码错误！");
        }
        if (ret == 0) {
            // 授权码正确
            return true;
        }

        log.error("校验失败！");
        return false;
    }


    public void testlicense() {

        Awesome.GoString.ByValue str = new Awesome.GoString.ByValue();
        str.p = "{\"machinecode\": \"CHTF-12H1-34S8-HV23-123S\", \"deadline\": 1, \"info\": {\"thj\":2}}";
        str.n = str.p.length();

        Awesome.GoString.ByValue code = new Awesome.GoString.ByValue();
        code.p = "a90c-7f6c-6489-542d-df0c";
        code.n = code.p.length();

        int ret = Awesome.INSTANCE.Verify(str, code);

        System.out.println("正确授权码返回" + ret);

        Awesome.GoString.ByValue wrong_code = new Awesome.GoString.ByValue();
        wrong_code.p = "190c-7f6c-6489-542d-df0c";
        wrong_code.n = wrong_code.p.length();

        ret = Awesome.INSTANCE.Verify(str, wrong_code);

        System.out.println("错误授权码返回" + ret);

    }


}
