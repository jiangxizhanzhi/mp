package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import com.alibaba.excel.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/27
 */
public class KaptchaUtil {

    public static ServiceResult<Object> checkKaptcha(String code) {
        ServiceResult<Object> result = null;
        HttpServletRequest request = ServletUtils.getRequest();
        HttpSession session = request.getSession();
        String kaptcha = (String) session.getAttribute(AuthConstant.KAPTCHA_SESSION_KEY);
        Long kaptchaDate = (Long) session.getAttribute(AuthConstant.KAPTCHA_SESSION_DATE);
        if (StringUtils.isBlank(code)) {
            result = ServiceResult.builder().source(false).serviceCode(AuthErrorCode.VERIFY_CODE_EMPTY).build();
        } else if (StringUtils.isBlank(kaptcha) || Objects.isNull(kaptchaDate) || System.currentTimeMillis() - kaptchaDate > AuthConstant.VALID_MILLIS_TIME) {
            result = ServiceResult.builder().source(Boolean.FALSE).serviceCode(AuthErrorCode.VERIFY_CODE_EXPIRE).build();
        } else if (!kaptcha.equalsIgnoreCase(code)) {
            result = ServiceResult.builder().source(false).serviceCode(AuthErrorCode.VERIFY_CODE_INCORRECT).build();
        }
        // 验证码校验成功
        if (Objects.isNull(result)) {
            result = ServiceResult.builder().source(true).build();
        }
        session.removeAttribute(AuthConstant.KAPTCHA_SESSION_KEY);
        session.removeAttribute(AuthConstant.KAPTCHA_SESSION_DATE);
        return result;
    }

    public static void checkFail(HttpServletResponse response, ServiceResult<Object> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        result = result.asResult();
        result.setDetail(result.getMessage());
        PrintWriter writer = response.getWriter();
        new ObjectMapper().writeValue(writer, result.asResult());
        writer.flush();
    }
}
