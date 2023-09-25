package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author litian
 * @date 2023年09月25 11:18
 */
@Component
public class BlacklistFilter extends OncePerRequestFilter {
    @Resource
    private BlacklistModel blacklistModel;

    @Resource
    private BaseAppConfig baseAppConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (blacklistModel.isEnabled()) {
            String method = request.getMethod();
            String uri = request.getServletPath();

            // 快速失败模式，不使用流过滤
            for (BlacklistModel.AccessItem item : blacklistModel.getPatterns()) {
                if (StringUtils.isEmpty(item.getMethod()) || method.equals(item.getMethod())) {
                    if (item.getPattern().matches(PathContainer.parsePath(uri))) {
                        denyAccess(uri);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void denyAccess(String uri) {
        throw new AuthenticationServiceException("The current access operation is not authorized: " + uri);
    }
}
