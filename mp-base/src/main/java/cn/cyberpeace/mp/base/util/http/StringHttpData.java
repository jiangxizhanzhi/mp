package cn.cyberpeace.mp.base.util.http;

import java.util.Map;

public class StringHttpData implements HttpData {
    private Object data;
    public StringHttpData(Object data) {
        this.data = data;
    }

    public String toContent() {
        return data.toString();
    }
    public Map<String, Object> toMultipart(Object httpData) {
        throw new UnsupportedOperationException();
    }
}
