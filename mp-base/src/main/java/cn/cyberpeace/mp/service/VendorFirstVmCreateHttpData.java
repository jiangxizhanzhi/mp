package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpData;
import com.alibaba.fastjson.JSONObject;

public class VendorFirstVmCreateHttpData implements HttpData {
    private final JSONObject jsonObject;
    public VendorFirstVmCreateHttpData(JSONObject object) {
        this.jsonObject = object;
    }

    @Override
    public String toContent() {
        return HttpData.allPropertyToContent(this.jsonObject);
    }
}
