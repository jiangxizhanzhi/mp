package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class VirtualMachineRestartData implements CloudHttpData {
    private Boolean waitForStatus = true;
    private Map reboot = new HashMap();

    public VirtualMachineRestartData() {
        this.reboot.put("reboot_type", "HARD");
    }
}
