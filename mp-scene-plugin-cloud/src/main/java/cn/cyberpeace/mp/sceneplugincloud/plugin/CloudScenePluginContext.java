package cn.cyberpeace.mp.sceneplugincloud.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CloudScenePluginContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudScenePluginContext {
    private String requestUser;

    private String requestUserOrg;
}
