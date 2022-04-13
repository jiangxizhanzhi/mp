package cn.cyberpeace.mp.auth.extensions.security.xss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/12 13:51
 */
public class XssFilter implements Filter {

    private List<String> excludes = new ArrayList<>();

    private boolean enabled = false;

    private List<XssBlackListItem> blacklist;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String strExcludes = filterConfig.getInitParameter("excludes");
        String strEnabled = filterConfig.getInitParameter("enabled");
        String blist = filterConfig.getInitParameter("blacklist");
        //将不需要xss过滤的接口添加到列表中
        if (StringUtils.isNotEmpty(strExcludes)) {
            String[] urls = strExcludes.split(",");
            for (String url : urls) {
                excludes.add(url);
            }
        }
        if (StringUtils.isNotEmpty(strEnabled)) {
            enabled = Boolean.valueOf(strEnabled);
        }

        blacklist = JSON.parseObject(blist, new TypeReference<List<XssBlackListItem>>(){});
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //如果该访问接口在排除列表里面则不拦截
        if (isExcludeUrl(request.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //拦截该url并进行xss过滤
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(request);
        xssHttpServletRequestWrapper.setBlacklist(blacklist);
        filterChain.doFilter(xssHttpServletRequestWrapper, servletResponse);

    }

    @Override
    public void destroy() {

    }

    private boolean isExcludeUrl(String urlPath) {
        if (!enabled) {
            //如果xss开关关闭了，则所有url都不拦截
            return true;
        }
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }

        String url = urlPath;
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }


}
