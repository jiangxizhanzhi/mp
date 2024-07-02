package cn.cyberpeace.mp.base.util.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @ClassName HttpDeleteWithBody
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpEntityEnclosingRequestBase
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    /**
     * HttpDeleteWithBody
     */
    public HttpDeleteWithBody() {
        super();
    }

    /**
     * HttpDeleteWithBody
     *
     * @param uri
     */
    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * HttpDeleteWithBody
     *
     * @param uri
     */
    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    /**
     * getMethod
     *
     * @return
     */
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
