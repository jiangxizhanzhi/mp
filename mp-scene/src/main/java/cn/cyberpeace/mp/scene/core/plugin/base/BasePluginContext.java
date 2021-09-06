package cn.cyberpeace.mp.scene.core.plugin.base;

import lombok.Data;

@Data
public class BasePluginContext<H> {
    private H sceneHandler;
}
