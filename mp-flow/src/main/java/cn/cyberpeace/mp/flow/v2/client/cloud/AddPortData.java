package cn.cyberpeace.mp.flow.v2.client.cloud;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
@Getter
@Setter
public class AddPortData implements SnakeHttpData {
    private String networkId;
}
