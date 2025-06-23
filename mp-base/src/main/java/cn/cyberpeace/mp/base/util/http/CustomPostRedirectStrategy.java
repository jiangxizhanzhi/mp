package cn.cyberpeace.mp.base.util.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * CustomPostRedirectStrategy
 *
 * @author Jerry(姜源)
 * @since 2025/06/12 19:22
 */

public class CustomPostRedirectStrategy extends LaxRedirectStrategy {
    @Override
    public HttpUriRequest getRedirect(
            HttpRequest request,
            HttpResponse response,
            HttpContext context) throws ProtocolException {

        // 1. 获取默认的重定向请求（此时方法可能已被转为GET）
        HttpUriRequest redirect = super.getRedirect(request, response, context);

        // 2. 如果是POST请求，重建请求并复制原始请求体
        if (request instanceof HttpPost) {
            HttpPost originalPost = (HttpPost) request;
            HttpPost newPost = new HttpPost(redirect.getURI());

            // 复制请求头
            newPost.setHeaders(redirect.getAllHeaders());

            // 复制请求体（关键！）
            if (originalPost.getEntity() != null) {
                newPost.setEntity(originalPost.getEntity());
            }

            return newPost;
        }

        return redirect;
    }
}

