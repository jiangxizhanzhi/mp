package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Data
public class SaveNeighborHoodResult extends IxiaResponse {
    private String name;
}
