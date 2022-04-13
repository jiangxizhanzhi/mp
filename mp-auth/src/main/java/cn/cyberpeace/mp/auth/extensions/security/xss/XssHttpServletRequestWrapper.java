package cn.cyberpeace.mp.auth.extensions.security.xss;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/13 09:54
 */
@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    boolean isUpData = false;//判断是否是上传 上传忽略

    private List<XssBlackListItem> blacklist;

    /*// 期待被过滤的url和字段 黑名单
    HashMap<String, List<String>> urlAndParamBlackList = new HashMap<String, List<String>>() {
        {
            put("/unauth/register", CollectionUtil.toList("name", "username", "nickname", "email"));
            put("/auth/users/add", CollectionUtil.toList("address", "name", "username", "nickname", "email", "description"));
            put("/auth/users/modify", CollectionUtil.toList("address", "name", "username", "nickname", "email", "description"));
            put("/auth/orgs", CollectionUtil.toList("name"));
            put("/auth/orgs/*", CollectionUtil.toList("name"));
            put("/auth/roles/add", CollectionUtil.toList("name"));
            put("/auth/roles/modify", CollectionUtil.toList("name"));

            put("/sys/config/update/bykeyname", CollectionUtil.toList("key_name", "value"));
            put("/sys/config/updateall", CollectionUtil.toList("key_name", "value"));

            put("/devops/log/operate/add", CollectionUtil.toList("description", "from", "op_target", "title", "user_name"));

        }
    };*/


    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        String contentType = request.getContentType();
        if (null != contentType)
            isUpData = contentType.startsWith("multipart");
    }

    /**
     * 过滤单个参数
     *
     * @param name
     * @return
     */
    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        if (StringUtils.isNotBlank(parameter)) {

            if (Objects.nonNull(getMatchedBlackListItem(name))) {
                String escapeParameter = cleanXSS(parameter);
                return escapeParameter;
            }

        }
        return parameter;
    }

    /**
     * 过滤实体的每个参数
     *
     * @param name
     * @return
     */
    @Override
    public String[] getParameterValues(String name) {

        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues == null) {
            return null;
        }

        if (Objects.nonNull(getMatchedBlackListItem(name))) {
            for (int i = 0; i < parameterValues.length; ++i) {
                String value = parameterValues[i];
                parameterValues[i] = cleanXSS(value);
            }
        }

        return parameterValues;

    }

    /**
     * 处理@RequestBody的形式传入的json数据
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {

        if (isUpData) {
            return super.getInputStream();
        } else {

            final ByteArrayInputStream bais = new ByteArrayInputStream(inputHandlers(super.getInputStream()).getBytes());

            return new ServletInputStream() {

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {

                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }

    }


    public String inputHandlers(ServletInputStream servletInputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(servletInputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        // todo 白名单

        boolean isUrlNeedFilter = false;
        List<String> fields = null;

        // 黑名单检查，requesturl在黑名单内，则进行过滤拦截
        XssBlackListItem item = getMatchedBlackListItem(null);
        if (Objects.nonNull(item)) {
            isUrlNeedFilter = true;
            fields = Arrays.asList(item.getFields().split(","));
        }

        if (isUrlNeedFilter) {   //有需要特殊处理的字段，不希望过滤标签
            try {
                JSONObject jsonObject = JSONObject.parseObject(sb.toString());

                for (String k : jsonObject.keySet()) {
                    if (fields.contains("*") || fields.contains(k)) {
                        String convertedFieldString = cleanXSS(JSONObject.toJSONString(jsonObject.get(k)));
                        Object convertedObject = JSONObject.parse(convertedFieldString);
                        jsonObject.put(k, convertedObject);
                    }
                }

                return jsonObject.toJSONString();

            } catch (Exception e) {
                log.error("XssHttpServletRequestWrapper转换json数据失败", e);
                return sb.toString();  //异常时，就直接过滤，不管需要特殊处理的参数
            }

        }

        return sb.toString();
    }


    public List<XssBlackListItem> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<XssBlackListItem> blacklist) {
        this.blacklist = blacklist;
    }

    /**
     * 过滤规则，这里不直接使用StringEscapeUtils.escapeHtml，因为获取的是一个json字符串，会将" 替换导致数据异常，没有""进行分割，无法正常注入到@RequestBody
     *
     * @param value
     * @return
     */
    private static String cleanXSS(String value) {
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("&", "&amp;");
        value = value.replaceAll("/", "&#x2F;");
//        value = value.replaceAll("%3C", "&lt;").replaceAll("%3E", "&gt;");
//        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
//        value = value.replaceAll("%28", "&#40;").replaceAll("%29", "&#41;");
//        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
//        value = value.replaceAll("script", "");
        return value;
    }

    /**
     * 是否是需要拦截的request method
     *
     * @return
     */
    private boolean isSupportedMethod(List<String> methods) {
        String method = this.getMethod();
        if (methods.contains(method)) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否需要进行xss转义
     * 1. url是否匹配
     * 2. 参数名称是否包含在fields内
     * 3. 请求method是否匹配
     *
     * @param paramName
     * @return
     */
    private XssBlackListItem getMatchedBlackListItem(String paramName) {
        String requestUrl = StringUtils.replaceOnce(this.getRequestURI(), this.getContextPath(), StringUtils.EMPTY);

        for (XssBlackListItem item : blacklist) {
            String pattern = item.getUrlpattern();
            if (antPathMatcher.match(pattern, requestUrl)) {
                if (StringUtils.isBlank(item.getFields().trim())) {
                    continue;
                }

                List<String> methods = Arrays.asList(item.getMethods().split(","));
                if (isSupportedMethod(methods)) {
                    // param为空，直接返回item
                    if (Objects.isNull(paramName)) {
                        return item;
                    }

                    // param不为空，匹配fields后再返回
                    List<String> fields = Arrays.asList(item.getFields().split(","));
                    if (fields.contains("*") || fields.contains(paramName)) {
                        // return first
                        return item;
                    }
                }

            }
        }

        return null;
    }

}
