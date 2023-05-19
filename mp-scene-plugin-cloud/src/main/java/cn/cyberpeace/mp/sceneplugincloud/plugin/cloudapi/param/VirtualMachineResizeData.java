package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineResizeData implements CloudHttpData {
    private Boolean waitForStatus = true;
    private Map<String, String> resize = new HashMap<>();

    public void setFlavorId(String flavorId) {
        this.resize.put("flavor", flavorId);
    }
}
