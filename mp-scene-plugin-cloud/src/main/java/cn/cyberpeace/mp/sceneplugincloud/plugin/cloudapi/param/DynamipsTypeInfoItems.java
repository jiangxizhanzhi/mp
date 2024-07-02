package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName DynamipsTypeInfoItems
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpParam
 */
@Data
public class DynamipsTypeInfoItems implements SnakeHttpParam {

    private Map<String, Object> items;

}
