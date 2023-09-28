package cn.cyberpeace.mp.collectionapi.bo;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import lombok.Data;

@Data
public class DeviceLogBO extends DeviceLog implements HttpData {

    public static void main(String[] args) {
        DeviceLogBO  bo = new DeviceLogBO();
        bo.setLogType("abc");
        bo.setId("id");
        System.out.println(JsonUtils.stringify(bo));
    }
}
