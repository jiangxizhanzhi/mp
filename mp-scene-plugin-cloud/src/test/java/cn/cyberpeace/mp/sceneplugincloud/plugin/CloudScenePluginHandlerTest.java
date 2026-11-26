package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class CloudScenePluginHandlerTest {
    @Test void delayedVendorAddressVerificationUpdatesOnlyVncDisplayAddress() {
        SceneElementPortBO port = new SceneElementPortBO();
        port.setIp("198.51.100.10");
        CloudPluginSceneElementPortBO plugin = new CloudPluginSceneElementPortBO();
        plugin.setSrcPort(port);
        CloudAccessMode vnc = new CloudAccessMode();
        vnc.setProtocol("vnc");
        vnc.setAccessIp("");
        vnc.setUsername("fixture");
        CloudAccessMode other = new CloudAccessMode();
        other.setProtocol("ssh");
        other.setAccessIp("198.51.100.20");
        plugin.setAccessModes(Arrays.asList(vnc, other));
        CloudScenePluginHandler.syncVendorSecondAccessIp(plugin);
        assertEquals("198.51.100.10", vnc.getAccessIp());
        assertEquals("fixture", vnc.getUsername());
        assertEquals("198.51.100.20", other.getAccessIp());
        port.setIp(null);
        CloudScenePluginHandler.syncVendorSecondAccessIp(plugin);
        assertEquals("", vnc.getAccessIp());
    }
}
