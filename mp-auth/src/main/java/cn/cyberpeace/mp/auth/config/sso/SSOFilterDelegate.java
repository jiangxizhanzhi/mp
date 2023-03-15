package cn.cyberpeace.mp.auth.config.sso;

import cn.hutool.core.exceptions.ExceptionUtil;
import edu.yale.its.tp.cas.client.filter.LogoutFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/3/15 11:39
 */
@Service
@Slf4j
public class SSOFilterDelegate {

    private LogoutFilter ssoLogoutFilter = new LogoutFilter();

    public void invokeSSOLogoutFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            ssoLogoutFilter.doFilter(request, response, null);
            log.info("ssoLogoutFilter invoke success");
        } catch (Exception e) {
            log.error("ssoLogoutFilter invoke error e:{}", ExceptionUtil.stacktraceToString(e));
        }
    }

}
