package cn.cyberpeace.mp.auth.test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
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
