package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

import java.util.Map;

@Data
public class DynamipsTypeInfoItems implements SnakeHttpParam {

    private Map<String, Object> items;

}
