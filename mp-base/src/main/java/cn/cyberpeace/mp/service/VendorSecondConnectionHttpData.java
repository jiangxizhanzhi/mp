package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

@Data
public class VendorSecondConnectionHttpData implements SnakeHttpData {

    private ConnectProtocol remoteConsole = new ConnectProtocol();

    @Data
    public static class ConnectProtocol {
        private String protocol = "vnc";
        private String type = "novnc";
    }
}
