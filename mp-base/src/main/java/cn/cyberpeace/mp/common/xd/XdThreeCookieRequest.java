package cn.cyberpeace.mp.common.xd;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

@Data
public class XdThreeCookieRequest implements HttpData {

    private String username = "Admin";
    private String password = "d64980e78f071a2cdc38a1df9119f287";
    private String realm = "hci";

}
