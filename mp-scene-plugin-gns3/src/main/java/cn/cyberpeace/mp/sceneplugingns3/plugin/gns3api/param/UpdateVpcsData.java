package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param;

import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

@Setter
@Getter
public class UpdateVpcsData implements Gns3HttpData {
    private String ip;
    private String gateway;

    @Override
    public String toContent() {
        return String.format("ip %s %s", ip, gateway);
    }

    @Override
    public String getContentType() {
        return MediaType.TEXT_PLAIN_VALUE;
    }

}
