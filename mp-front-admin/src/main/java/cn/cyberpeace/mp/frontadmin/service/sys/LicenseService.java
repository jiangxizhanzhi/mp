 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LicenseService.java.
 // File size 3777.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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

 /**
  * 许可证服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class LicenseService {

     /**
      * 实例
      */
     private static Awesome INSTANCE = null;

     /**
      * 前面管理配置
      */
     @Autowired
    private FrontAdminConfig frontAdminConfig;

     /**
      * 很棒
      *
      * @author 二竹
      * @date 2025/01/17
      */
     public interface Awesome extends Library {

//        Awesome INSTANCE = (Awesome) Native.loadLibrary("/home/mp/config/license/license.so", Awesome.class);

         /**
          * @author 二竹
          * @date 2025/01/17
          */
         public class GoString extends Structure {
             /**
              * 价值
              *
              * @author 二竹
              * @date 2025/01/17
              */
             public static class ByValue extends GoString implements Structure.ByValue {
            }

             /**
              * p
              */
             public String p;
             /**
              * n
              */
             public long n;

             /**
              * @return {@link List }
              */
             protected List getFieldOrder() {
                return Arrays.asList(new String[]{"p", "n"});
            }

        }

         /**
          * 验证
          *
          * @param js   js
          * @param code 代码
          * @return int
          */
         public int Verify(GoString.ByValue js, GoString.ByValue code);
    }

     /**
      * 验证
      *
      * @param verifyStr  校验json串，如 "
      * @param verifyCode 校验码
      * @return
      */
     public boolean verify(String verifyStr, String verifyCode) {
//        verifyStr = "{\"machinecode\": \"CHTF-12H1-34S8-HV23-123S\", \"deadline\": 1, \"info\": {\"thj\":2}}";
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

//        INSTANCE = (Awesome) Native.loadLibrary(activationConfig.getLicenseDir() + File.separator + "license.so", Awesome.class);
        log.info("Awesome INSTANCE:{}", INSTANCE);
        int ret = INSTANCE.Verify(str, code);
        log.info("verify complete! ret:{}", ret);

        //  -1	json传入错误
        //  0	此授权码正确
        //  1	此授权码错误
        if (ret == -1) {
            log.error("json error!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
        }
        if (ret == 1) {
            log.error("verifyCode error!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
        }
        if (ret == 0) {
            // 授权码正确
            log.info("activation success!");
            return true;
        }

        log.error("activation failed!");
        return false;
    }

}
