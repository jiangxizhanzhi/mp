package cn.cyberpeace.mp.sceneplugincloud.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudScenePluginContext {
    private String requestUser;

    private String requestUserOrg;
}
