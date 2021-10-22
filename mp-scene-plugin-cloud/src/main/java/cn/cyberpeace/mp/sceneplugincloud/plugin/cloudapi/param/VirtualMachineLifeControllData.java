package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineLifeControllData implements CloudHttpData {
    private Map pause;
    private Map unpause;
    private Map reboot;
    private Boolean waitForStatus;

    public void setPause(Map pause) {
        this.pause = pause;
    }

    public void setUnpause(Map unpause) {
        this.unpause = unpause;
    }

    public void setRestart(Map reboot) {
        this.reboot = reboot;
    }

    public void setWaitForStatus(Boolean waitForStatus) {
        this.waitForStatus = waitForStatus;
    }
}
