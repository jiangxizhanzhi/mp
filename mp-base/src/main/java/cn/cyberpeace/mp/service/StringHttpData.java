package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

@Data
public class StringHttpData implements HttpData {

    private String data;

    public StringHttpData(String content) {
        this.data = content;
    }

    public String toContent() {
        return data;
    }
}
