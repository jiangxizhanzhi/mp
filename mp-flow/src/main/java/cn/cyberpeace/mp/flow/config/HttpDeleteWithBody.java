package cn.cyberpeace.mp.flow.config;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import javax.annotation.concurrent.NotThreadSafe;
import java.net.URI;


@NotThreadSafe
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = "DELETE";

    
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithBody() {
        super();
    }
}
