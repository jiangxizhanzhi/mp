package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

@Data
public class DynamipsInfoParam implements SnakeHttpParam {
    private String type;
}
