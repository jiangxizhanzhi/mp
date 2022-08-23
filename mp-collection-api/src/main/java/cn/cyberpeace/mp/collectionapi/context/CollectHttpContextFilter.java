package cn.cyberpeace.mp.collectionapi.context;

import cn.cyberpeace.mp.collection.context.ContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CollectHttpContextFilter extends OncePerRequestFilter {
    private static final String TEAM_ID = "teamId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            var groupId = request.getParameter(TEAM_ID);
            if (StringUtils.isNotEmpty(groupId)) {
                ContextHolder.ContextBo context = new ContextHolder.ContextBo();
                context.setGroupId(groupId);
                ContextHolder.setContext(context);
            }
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        } finally {
            ContextHolder.clear();
        }
    }
}
