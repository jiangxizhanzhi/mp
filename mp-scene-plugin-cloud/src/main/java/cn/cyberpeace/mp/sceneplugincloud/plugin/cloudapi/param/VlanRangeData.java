package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VlanRangeData implements CloudHttpData {
    private String vlans;
}
