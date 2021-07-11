package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class LicenseService {

    private static Awesome INSTANCE = null;

    @Autowired
    private FrontAdminConfig frontAdminConfig;

    public interface Awesome extends Library {



        public class GoString extends Structure {
            public static class ByValue extends GoString implements Structure.ByValue {
            }

            public String p;
            public long n;

            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"p", "n"});
            }

        }

        public int Verify(GoString.ByValue js, GoString.ByValue code);
    }


    
    public boolean verify(String verifyStr, String verifyCode) {

        if (StringUtils.isEmpty(verifyCode)) {
            log.info("verifyCode is empty!");
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }
        Awesome.GoString.ByValue str = new Awesome.GoString.ByValue();
        str.p = verifyStr;
        str.n = str.p.length();

        Awesome.GoString.ByValue code = new Awesome.GoString.ByValue();
        code.p = verifyCode;
        code.n = code.p.length();

        if (Objects.isNull(INSTANCE)) {
            log.info("init INSTANCE...");
            try {
                INSTANCE = (Awesome) Native.loadLibrary(frontAdminConfig.getLicenseDir() + File.separator + "license.so", Awesome.class);
                log.info("load license.so success!");
            } catch (Exception e) {
                log.error("load license.so error, {}", ExceptionUtils.getStackTrace(e));
                throw e;
            }
        }


        log.info("Awesome INSTANCE:{}", INSTANCE);
        int ret = INSTANCE.Verify(str, code);
        log.info("verify complete! ret:{}", ret);

        
        
        
        if (ret == -1) {
            log.error("json error!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
        }
        if (ret == 1) {
            log.error("verifyCode error!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
        }
        if (ret == 0) {
            
            log.info("activation success!");
            return true;
        }

        log.error("activation failed!");
        return false;
    }

}
