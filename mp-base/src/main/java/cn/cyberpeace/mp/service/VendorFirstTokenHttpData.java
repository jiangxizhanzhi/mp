package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpData;
import com.alibaba.fastjson.JSONObject;

public class VendorFirstTokenHttpData implements HttpData {

    private final String data;
    public VendorFirstTokenHttpData(String data) {
        this.data = data;
    }

    @Override
    public String toContent() {
        return HttpData.allPropertyToContent(JSONObject.parse(data));
    }
}
